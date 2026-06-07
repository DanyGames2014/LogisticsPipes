package net.danygames2014.logisticspipes.block.entity;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.danygames2014.buildcraft.block.PipeBlock;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.logisticspipes.LogisticsPipes;
import net.danygames2014.logisticspipes.block.pipe.ItemSendMode;
import net.danygames2014.logisticspipes.capability.recipeprovider.CraftingPipeRecipeProviderBlockCapability;
import net.danygames2014.logisticspipes.entity.RoutedItemEntity;
import net.danygames2014.logisticspipes.gui.hud.CraftingHud;
import net.danygames2014.logisticspipes.interfaces.*;
import net.danygames2014.logisticspipes.interfaces.routing.CraftItems;
import net.danygames2014.logisticspipes.network.CraftingPipeCommandC2SPacket;
import net.danygames2014.logisticspipes.network.UpdateOrderManagerContentS2CPacket;
import net.danygames2014.logisticspipes.network.UpdatePipeChestContentS2CPacket;
import net.danygames2014.logisticspipes.network.UpdatePlayerWatchingStatusC2SPacket;
import net.danygames2014.logisticspipes.request.CraftingTemplate;
import net.danygames2014.logisticspipes.request.RequestManager;
import net.danygames2014.logisticspipes.request.RequestTreeNode;
import net.danygames2014.logisticspipes.routing.LogisticsNetworkManager;
import net.danygames2014.logisticspipes.routing.LogisticsOrderManager;
import net.danygames2014.logisticspipes.routing.LogisticsPromise;
import net.danygames2014.logisticspipes.routing.Router;
import net.danygames2014.logisticspipes.screen.handler.CraftingPipeScreenHandler;
import net.danygames2014.logisticspipes.util.*;
import net.danygames2014.logisticspipes.util.tuple.Pair;
import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.danygames2014.nyalib.capability.block.itemhandler.ItemHandlerBlockCapability;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.*;

public class CraftingLogisticPipeBlockEntity extends LogisticPipeBlockEntity implements CraftItems, HUDRendererProvider, OrderManagerContentReceiver, RequireReliableTransport {
    protected LogisticsOrderManager orderManager = new LogisticsOrderManager(this::onChange);

    public final LinkedList<ItemIdentifierStack> oldList = new LinkedList<>();
    public final LinkedList<ItemIdentifierStack> displayList = new LinkedList<>();
    public final PlayerCollectionList localModeWatchers = new PlayerCollectionList();
    private final CraftingHud HUD = new CraftingHud(this);

    protected int extras;
    private boolean init = false;

    // logic stuff
    protected SimpleInventory dummyInventory = new SimpleInventory(10, "Requested items", 127, this::markInventoryDirty);

    protected final LinkedList<ItemIdentifier> lostItems = new LinkedList<>();
    public int satelliteId = 0;
    public int priority = 0;

    public CraftingLogisticPipeBlockEntity() {
        super();
    }

    public CraftingLogisticPipeBlockEntity(PipeBlock pipeBlock) {
        super(pipeBlock);
    }

    @Override
    public void setup() {
        throttleTime = 40;
    }

    protected LinkedList<AdjacentBlockEntity> locateCrafters() {
        LinkedList<AdjacentBlockEntity> crafters = new LinkedList<>();
        for (AdjacentBlockEntity adjacent : WorldUtil.getAdjacentBlockEntities(world, x, y, z)) {
            if (adjacent.blockEntity instanceof PipeBlockEntity) continue;

            ItemHandlerBlockCapability capability = CapabilityHelper.getCapability(adjacent.blockEntity, ItemHandlerBlockCapability.class);
            if (capability == null) continue;

            crafters.add(adjacent);
        }
        return crafters;
    }

    protected ItemStack extractFromInventory(Inventory inv) {
        InventoryUtil invUtil = new InventoryUtil(inv, false);
        ItemStack itemstack = getCraftedItemLogic();
        if (itemstack == null) return null;

        ItemIdentifierStack targetItemStack = ItemIdentifierStack.getFromStack(itemstack);
        return invUtil.getSingleItem(targetItemStack.getItem());
    }

    @Override
    public void openModuleScreen(PlayerEntity player) {
        GuiHelper.openGUI(player, LogisticsPipes.NAMESPACE.id("crafting"), getDummyInventory(), new CraftingPipeScreenHandler(player, this), (messagePacket) -> {
            messagePacket.ints = new int[]{messagePacket.ints != null ? messagePacket.ints[0] : 0, x, y, z};
        });
    }

    public void enableUpdateRequest() {
        init = false;
    }

    @Override
    public void tick() {
        super.tick();
        if (!init) {
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
//                if(FMLClientHandler.instance().getClient() != null && FMLClientHandler.instance().getClient().thePlayer != null && FMLClientHandler.instance().getClient().thePlayer.sendQueue != null){
//                    PacketDispatcher.sendPacketToServer(new PacketCoordinates(NetworkConstants.REQUEST_CRAFTING_PIPE_UPDATE, xCoord, yCoord, zCoord).getPacket());
//                }
            }
            init = true;
        }

        if (this instanceof CraftingLogisticPipeBlockEntityMk2) {
            return;
        }

        if ((!orderManager.hasOrders() && extras < 1) || world.getTime() % 6 != 0) {
            return;
        }

        queueParticle(ParticleColor.VIOLET, 2);

        LinkedList<AdjacentBlockEntity> crafters = locateCrafters();
        if (crafters.isEmpty()) {
            orderManager.sendFailed();
            return;
        }

        for (AdjacentBlockEntity adjacent : locateCrafters()) {
            ItemStack extracted = null;

            if (NyalibInventoryUtil.hasItemHandler(adjacent.blockEntity)) {
                extracted = extractFromInventory(NyalibInventoryUtil.getWrappedItemHandler(adjacent.blockEntity, adjacent.direction.getOpposite()));
            }

            if (extracted == null) continue;
            while (extracted.count > 0) {
                ItemStack stackToSend = extracted.split(1);
//                Position p = new Position(adjacent.tile.xCoord, adjacent.tile.yCoord, adjacent.tile.zCoord, adjacent.orientation);
                if (orderManager.hasOrders()) {
                    Pair<ItemIdentifierStack, RequestItems> order = orderManager.getNextRequest();
                    RoutedItem item = ItemUtil.createRoutedItem(stackToSend, world);
                    item.setSource(this.getRouter().getRouterId());
                    item.setDestination(order.getValue2().getRouter().getRouterId());
                    item.setTransportMode(RoutedItem.TransportMode.Active);
                    super.queueRoutedItem(item, adjacent.direction);
                    orderManager.sendSuccessfull(1);
                } else {
                    extras--;
                    // TODO: put shit in pipes
//                    if(LogisticsPipes.DisplayRequests)System.out.println("Extra dropped, " + _extras + " remaining");
//                    Position entityPos = new Position(p.x + 0.5, p.y + Utils.getPipeFloorOf(stackToSend), p.z + 0.5, p.orientation.reverse());
//                    entityPos.moveForwards(0.5);
//                    EntityPassiveItem entityItem = new EntityPassiveItem(worldObj, entityPos.x, entityPos.y, entityPos.z, stackToSend);
//                    entityItem.setSpeed(Utils.pipeNormalSpeed * Configs.LOGISTICS_DEFAULTROUTED_SPEED_MULTIPLIER);
//                    ((PipeTransportItems) transport).entityEntering(entityItem, entityPos.orientation);
                }
            }
        }
    }

    private ItemIdentifier providedItem() {
        ItemStack stack = getCraftedItemLogic();
        if (stack == null) return null;
        return ItemIdentifier.get(stack);
    }

    @Override
    public void canProvide(RequestTreeNode tree, Map<ItemIdentifier, Integer> donePromisses) {
        if (!isEnabled()) {
            return;
        }

        smartAdvertiseRouter();

        if (extras < 1) return;
        ItemIdentifier providedItem = providedItem();
        if (tree.getStack().getItem() != providedItem) return;
        int alreadyPromised = donePromisses.getOrDefault(providedItem, 0);
        if (alreadyPromised >= extras) return;
        int remaining = extras - alreadyPromised;
        LogisticsPromise promise = new LogisticsPromise();
        promise.item = providedItem;
        promise.numberOfItems = Math.min(remaining, tree.getMissingItemCount());
        promise.sender = this;
        promise.extra = true;
        tree.addPromise(promise);
    }

    @Override
    public void addCrafting(LinkedList<CraftingTemplate> crafters) {
        if (!isEnabled()) {
            return;
        }

        ItemStack stack = getCraftedItemLogic();
        if (stack == null) return;

        CraftingTemplate template = new CraftingTemplate(ItemIdentifierStack.getFromStack(stack), this, priority);

        // Check all materials
        boolean hasSatellite = isSatelliteConnected();
        for (int i = 0; i < 9; i++) {
            ItemStack resourceStack = getMaterials(i);
            if (resourceStack == null || resourceStack.count == 0) continue;
            if (i < 6 || !hasSatellite) {
                template.addRequirement(ItemIdentifierStack.getFromStack(resourceStack), this);
            } else {
                getSatelliteRouter().smartAdvertiseRouter();
                template.addRequirement(ItemIdentifierStack.getFromStack(resourceStack), getSatelliteRouter().getPipe());
            }

        }
        crafters.add(template);
    }

    @Override
    public void fullFill(LogisticsPromise promise, RequestItems destination) {
        if (promise.extra) {
            extras -= promise.numberOfItems;
        }
        
        orderManager.addOrder(new ItemIdentifierStack(promise.item, promise.numberOfItems), destination);
        queueParticle(ParticleColor.WHITE, 2);
    }

    @Override
    public int getAvailableItemCount(ItemIdentifier item) {
        return 0;
    }

    @Override
    public void registerExtras(int count) {
        extras += count;
//        if(LogisticsPipes.DisplayRequests)System.out.println(count + " extras registered");
    }

    @Override
    public HashMap<ItemIdentifier, Integer> getAllItems() {
        return new HashMap<>();
    }

    @Override
    public ItemIdentifier getCraftedItem() {
        if (!isEnabled()) {
            return null;
        }
        return providedItem();
    }

    @Override
    public LogisticsModule getLogisticsModule() {
        return null;
    }

    // TODO: maybe later
//    public boolean isAttachedSign(TileEntity entity) {
//        return entity.xCoord == ((BaseLogicCrafting)logic).signEntityX && entity.yCoord == ((BaseLogicCrafting)logic).signEntityY && entity.zCoord == ((BaseLogicCrafting)logic).signEntityZ;
//    }
//
//    public void addSign(LogisticsSignTileEntity entity, EntityPlayer player) {
//        if(((BaseLogicCrafting)logic).signEntityX == 0 && ((BaseLogicCrafting)logic).signEntityY == 0 && ((BaseLogicCrafting)logic).signEntityZ == 0) {
//            ((BaseLogicCrafting)logic).signEntityX = entity.xCoord;
//            ((BaseLogicCrafting)logic).signEntityY = entity.yCoord;
//            ((BaseLogicCrafting)logic).signEntityZ = entity.zCoord;
//            PacketDispatcher.sendPacketToPlayer(new PacketPipeUpdate(NetworkConstants.PIPE_UPDATE,xCoord,yCoord,zCoord,getLogisticsNetworkPacket()).getPacket(), (Player)player);
//            final PacketInventoryChange newpacket = new PacketInventoryChange(NetworkConstants.CRAFTING_PIPE_IMPORT_BACK, xCoord, yCoord, zCoord, ((BaseLogicCrafting)logic).getDummyInventory());
//            PacketDispatcher.sendPacketToPlayer(newpacket.getPacket(), (Player)player);
//        }
//    }
//
//    public boolean canRegisterSign() {
//        return ((BaseLogicCrafting)logic).signEntityX == 0 && ((BaseLogicCrafting)logic).signEntityY == 0 && ((BaseLogicCrafting)logic).signEntityZ == 0;
//    }
//
//    public void removeRegisteredSign() {
//        ((BaseLogicCrafting)logic).signEntityX = 0;
//        ((BaseLogicCrafting)logic).signEntityY = 0;
//        ((BaseLogicCrafting)logic).signEntityZ = 0;
//        if(MainProxy.isServer()) {
//            MainProxy.sendToPlayerList(new PacketPipeUpdate(NetworkConstants.PIPE_UPDATE,xCoord,yCoord,zCoord,this.getLogisticsNetworkPacket()).getPacket(), localModeWatchers);
//        }
//    }


    @Override
    public ItemSendMode getItemSendMode() {
        return ItemSendMode.Normal;
    }

    public boolean hasOrder() {
        return orderManager.hasOrders();
    }

    public void onChange() {
        LinkedList<ItemIdentifierStack> all = orderManager.getContentList(world);
        if (!oldList.equals(all)) {
            oldList.clear();
            oldList.addAll(all);
            PacketUtil.sendToPlayerList(new UpdateOrderManagerContentS2CPacket(x, y, z, all), localModeWatchers);
        }
    }

    @Override
    public void setOrderManagerContent(LinkedList<ItemIdentifierStack> list) {
        displayList.clear();
        displayList.addAll(list);
    }

    @Override
    public HUDRenderer getRenderer() {
        return HUD;
    }

    @Override
    public void itemDropped(RoutedItemEntity routedItemEntity) {

    }

    @Override
    public LogisticPipeBlockEntity getPipe() {
        return this;
    }

    private void markInventoryDirty() {
        
    }

    /* SATELLITE CODE */
    protected int getNextConnectSatelliteId(boolean prev) {
        final ObjectOpenHashSet<Router> routes = getRouter().getNetwork().routers;
        int closestIdFound = prev ? 0 : Integer.MAX_VALUE;
        for (final SatelliteLogisticPipeBlockEntity satellite : SatelliteLogisticPipeBlockEntity.AllSatellites) {
            if (routes.contains(satellite.getRouter())) {
                if (!prev && satellite.satelliteId > satelliteId && satellite.satelliteId < closestIdFound) {
                    closestIdFound = satellite.satelliteId;
                } else if (prev && satellite.satelliteId < satelliteId && satellite.satelliteId > closestIdFound) {
                    closestIdFound = satellite.satelliteId;
                }
            }
        }
        
        if (closestIdFound == Integer.MAX_VALUE) {
            return satelliteId;
        }

        return closestIdFound;

    }

    public void setNextSatellite(PlayerEntity player) {
        if (player.world.isRemote) {
            PacketHelper.send(new CraftingPipeCommandC2SPacket(0));
            return;
        }

        satelliteId = getNextConnectSatelliteId(false);
    }

    public void setPrevSatellite(PlayerEntity player) {
        if (player.world.isRemote) {
            PacketHelper.send(new CraftingPipeCommandC2SPacket(1));
            return;
        }

        satelliteId = getNextConnectSatelliteId(true);
    }

    public boolean isSatelliteConnected() {
        for (final SatelliteLogisticPipeBlockEntity satellite : SatelliteLogisticPipeBlockEntity.AllSatellites) {
            if (satellite.satelliteId == satelliteId) {
                if (getRouter().getNetwork().routers.contains(satellite.getRouter())) {
                    return true;
                }
            }
        }
        
        return false;
    }

    public Router getSatelliteRouter() {
        for (final SatelliteLogisticPipeBlockEntity satellite : SatelliteLogisticPipeBlockEntity.AllSatellites) {
            if (satellite.satelliteId == satelliteId) {
                return satellite.getRouter();
            }
        }
        
        return null;
    }

    @Override
    public void throttledUpdateEntity() {
        super.throttledUpdateEntity();
        if (lostItems.isEmpty()) {
            return;
        }
        
        lostItems.removeIf(itemIdentifier -> RequestManager.request(itemIdentifier.makeStack(1), this, LogisticsNetworkManager.fetchRoutersByMetric(world, this), null));
    }

    @Override
    public void itemArrived(ItemIdentifier item) {
        
    }

    @Override
    public void itemLost(ItemIdentifier item) {
        lostItems.add(item);
    }

    public void openAttachedGui(PlayerEntity player) {
        if (world.isRemote) {
            PacketHelper.send(new CraftingPipeCommandC2SPacket(4));
            return;
        }

        boolean found = false;
        for (final AdjacentBlockEntity adjacent : WorldUtil.getAdjacentBlockEntities(world, x, y, z)) {
            // TODO: convert to the recipe provider capability
//            for (CraftingRecipeProvider provider : SimpleServiceLocator.craftingRecipeProviders) {
//                if (provider.canOpenGui(blockEntity.tile)) {
//                    found = true;
//                    break;
//                }
//            }

            if (!found)
                found = (NyalibInventoryUtil.hasItemHandler(adjacent.blockEntity) && !(adjacent.blockEntity instanceof PipeBlockEntity));

            if (found) {
                Block block = world.getBlockState(adjacent.blockEntity.x, adjacent.blockEntity.y, adjacent.blockEntity.z).getBlock();
                if (block != null) {
                    if (block.onUse(world, adjacent.blockEntity.x, adjacent.blockEntity.y, adjacent.blockEntity.z, player)) {
                        break;
                    }
                }
            }
        }
    }

    public void importFromCraftingTable(PlayerEntity player) {
        if (player.world.isRemote) {
            PacketHelper.send(new CraftingPipeCommandC2SPacket(3));
            return;
        }

        for (Direction side : Direction.values()) {
            BlockEntity blockEntity = world.getBlockEntity(this.x + side.getOffsetX(), this.y + side.getOffsetY(), this.z + side.getOffsetZ());
            if (blockEntity == null) {
                continue;
            }

            CraftingPipeRecipeProviderBlockCapability cap = CapabilityHelper.getCapability(blockEntity, CraftingPipeRecipeProviderBlockCapability.class);
            if (cap == null || !cap.canOpen(player)) {
                continue;
            }

            CraftingPipeRecipe recipe = cap.getRecipe();
            if (recipe == null) {
                continue;
            }

            // Set the recipe
            this.dummyInventory.setStack(9, recipe.output);
            int maxI = Math.min(9, recipe.inputs.size());
            for (int i = 0; i < maxI; i++) {
                this.dummyInventory.setStack(i, recipe.inputs.get(i));
            }

            break;
        }
    }

    public void handleStackMove(int number) {
        if (world.isRemote) {
//            PacketDispatcher.sendPacketToServer(new PacketPipeInteger(NetworkConstants.CRAFTING_PIPE_STACK_MOVE,xCoord,yCoord,zCoord,number).getPacket());
        }
        
        ItemStack stack = dummyInventory.getStack(number);
        if (stack == null) return;
        for (int i = 6; i < 9; i++) {
            ItemStack stackb = dummyInventory.getStack(i);
            if (stackb == null) {
                dummyInventory.setStack(i, stack);
                dummyInventory.setStack(number, null);
                break;
            }
        }
    }

    public void priorityUp(PlayerEntity player) {
        if (player.world.isRemote) {
            PacketHelper.send(new CraftingPipeCommandC2SPacket(20));
        }

        priority++;
    }

    public void priorityDown(PlayerEntity player) {
        if (player.world.isRemote) {
            PacketHelper.send(new CraftingPipeCommandC2SPacket(21));
        }

        priority--;
    }

    public void setPriority(int amount) {
        priority = amount;
    }

    /* ** INTERFACE TO PIPE ** */
    public ItemStack getCraftedItemLogic() {
        return dummyInventory.getStack(9);
    }

    public ItemStack getMaterials(int slotnr) {
        return dummyInventory.getStack(slotnr);
    }

    public SimpleInventory getDummyInventory() {
        return dummyInventory;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        dummyInventory.writeNbt(nbt, "crafting_pipe_inventory_");
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        dummyInventory.readNbt(nbt, "crafting_pipe_inventory_");
    }

    @Override
    public void startWatching() {
        PacketHelper.send(new UpdatePlayerWatchingStatusC2SPacket(x, y, z, 1, true));
    }

    @Override
    public void stopWatching() {
        PacketHelper.send(new UpdatePlayerWatchingStatusC2SPacket(x, y, z, 1, false));
    }

    @Override
    public void playerStartWatching(PlayerEntity player, int mode) {
        if(mode == 1) {
            localModeWatchers.add(player);
            PacketHelper.sendTo(player, new UpdateOrderManagerContentS2CPacket(x, y, z, orderManager.getContentList(world)));
        } else {
            super.playerStartWatching(player, mode);
        }
    }

    @Override
    public void playerStopWatching(PlayerEntity player, int mode) {
        super.playerStopWatching(player, mode);
        localModeWatchers.remove(player);
    }
}
