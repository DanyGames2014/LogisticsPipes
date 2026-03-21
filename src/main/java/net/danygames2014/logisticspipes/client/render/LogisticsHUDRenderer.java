package net.danygames2014.logisticspipes.client.render;

import net.danygames2014.logisticspipes.interfaces.HUDBlockRendererProvider;
import net.danygames2014.logisticspipes.interfaces.HUDRendererProvider;
import net.danygames2014.logisticspipes.util.MathVector;
import net.danygames2014.logisticspipes.util.tuple.Pair;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResultType;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class LogisticsHUDRenderer {
    private static LogisticsHUDRenderer INSTANCE = new LogisticsHUDRenderer();

    private LinkedList<HUDRendererProvider> list = new LinkedList<>();

    private double lastXPos = 0;
    private double lastYPos = 0;
    private double lastZPos = 0;

    private int progress = 0;
    private long last = 0;

    private ArrayList<HUDBlockRendererProvider> providers = new ArrayList<>();

    public static LogisticsHUDRenderer getInstance(){
        return INSTANCE;
    }

    private void add(HUDBlockRendererProvider provider) {
        HUDBlockRendererProvider toRemove = null;
        for(HUDBlockRendererProvider listedProvider : providers){
            if(listedProvider.getX() == provider.getX() && listedProvider.getY() == provider.getY() && listedProvider.getZ() == provider.getZ()) {
                toRemove = listedProvider;
                break;
            }
        }
        if(toRemove != null) {
            providers.remove(toRemove);
        }
        providers.add(provider);
    }

    public void remove(HUDBlockRendererProvider provider) {
        providers.remove(provider);
    }

    public void clear(){
        providers.clear();
        clearList(false);
    }

    private void clearList(boolean flag) {
        if(flag) {
            for(HUDRendererProvider renderer : list) {
                renderer.stopWatching();
            }
        }
        list.clear();
    }

    private void refreshList(double x,double y,double z) {
        ArrayList<Pair<Double,HUDRendererProvider>> newList = new ArrayList<>();
        for(Object blockEntity : Minecraft.INSTANCE.world.blockEntities){
            if(blockEntity instanceof HUDRendererProvider provider && blockEntity instanceof BlockEntity be){
                double dis = Math.hypot(be.x - x + 0.5,Math.hypot(be.y - y + 0.5, be.z - z + 0.5));
                if(dis < 40 && dis > 0.75) { //Configs.LOGISTICS_HUD_RENDER_DISTANCE
                    newList.add(new Pair<>(dis, provider));
                    if(!list.contains(provider)) {
                        provider.startWatching();
                    }
                }
            }
        }

        List<HUDBlockRendererProvider> remove = new ArrayList<>();
        for(HUDBlockRendererProvider provider : providers) {
            if(provider.getWorld() == Minecraft.INSTANCE.player.world){
                double dis = Math.hypot(provider.getX() - x + 0.5,Math.hypot(provider.getY() - y + 0.5, provider.getZ() - z + 0.5));
                if(dis < 40 && dis > 0.75 && !provider.isHUDInvalid() && provider.isHUDExistent()) { // Configs.LOGISTICS_HUD_RENDER_DISTANCE
                    newList.add(new Pair<>(dis, provider));
                    if(!list.contains(provider)) {
                        provider.startWatching();
                    }
                } else if(provider.isHUDInvalid() || !provider.isHUDExistent()) {
                    remove.add(provider);
                }
            }
        }

        for(HUDBlockRendererProvider provider : remove) {
            providers.remove(provider);
        }

        if(newList.isEmpty()) {
            clearList(true);
            return;
        }

        newList.sort((o1, o2) -> {
            if (o1.getValue1() < o2.getValue1()) {
                return -1;
            } else if (o1.getValue1() > o2.getValue1()) {
                return 1;
            } else {
                return 0;
            }
        });

        for(HUDRendererProvider part : list) {
            boolean contains = false;
            for(Pair<Double,HUDRendererProvider> inpart : newList) {
                if(inpart.getValue2().equals(part)) {
                    contains = true;
                    break;
                }
            }
            if(!contains) {
                part.stopWatching();
            }
        }

        clearList(false);
        for (Pair<Double, HUDRendererProvider> part : newList) {
            list.addLast(part.getValue2());
        }
    }

    public boolean displayRenderer() {
        if(!displayHUD()) {
            if(!list.isEmpty()) {
                clearList(true);
            }
        }
        return displayHUD();
    }

    private boolean displayHUD() {
        return (shouldPlayerShowHud() /*debugHUD != null*/) && Minecraft.INSTANCE.currentScreen == null && !Minecraft.INSTANCE.options.thirdPerson && !Minecraft.INSTANCE.options.hideHud;
    }

    private boolean shouldPlayerShowHud(){
        return Minecraft.INSTANCE.player != null
                && Minecraft.INSTANCE.player.inventory != null
                && Minecraft.INSTANCE.player.inventory.armor != null
                && Minecraft.INSTANCE.player.inventory.armor[3] != null
                && shouldItemStackShowHud(Minecraft.INSTANCE.player.inventory.armor[3]);
    }

    // TODO: this should check debug goggles or something else
    private boolean shouldItemStackShowHud(ItemStack stack){
        return stack.itemId == Item.LEATHER_HELMET.id;
    }

    private boolean displayCross = false;

    public void renderWorldRelative(long renderTicks, float partialTick) {
        if(!displayRenderer()) return;

        PlayerEntity player = Minecraft.INSTANCE.player;

        if(list.isEmpty() || Math.hypot(lastXPos - player.x,Math.hypot(lastYPos - player.y, lastZPos - player.z)) > 0.5 || (renderTicks % 10 == 0 && (lastXPos != player.x || lastYPos != player.y || lastZPos != player.z)) || renderTicks % 600 == 0) {
            refreshList(player.x,player.y,player.z);
            lastXPos = player.x;
            lastYPos = player.y;
            lastZPos = player.z;
        }

        boolean cursorHandled = false;
        displayCross = false;

        HUDRendererProvider thisIsLast = null;
        List<HUDRendererProvider> toUse = list;
//        if(debugHUD != null) toUse = debugHUD.getHUDs();
        for(HUDRendererProvider renderer : toUse) {
            if(renderer.getRenderer() == null) continue;
            if(renderer.getRenderer().display()) {
                GL11.glPushMatrix();
                if(!cursorHandled) {
                    double x = renderer.getX() + 0.5 - player.x;
                    double y = renderer.getY() + 0.5 - player.y;
                    double z = renderer.getZ() + 0.5 - player.z;
                    if(Math.hypot(x,Math.hypot(y, z)) < 0.75 || (renderer instanceof HUDBlockRendererProvider hudBlockRendererProvider && (hudBlockRendererProvider.isHUDInvalid() || !hudBlockRendererProvider.isHUDExistent()))) {
                        refreshList(player.x,player.y,player.z);
                        GL11.glPopMatrix();
                        break;
                    }
                    int[] pos = getCursor(renderer);
                    if(pos.length == 2) {
                        if(renderer.getRenderer().cursorOnWindow(pos[0], pos[1])) {
                            renderer.getRenderer().handleCursor(pos[0], pos[1]);
                            if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                                thisIsLast = renderer;
                                displayCross = true;
                            }
                            cursorHandled = true;
                        }
                    }
                }
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                if(thisIsLast != renderer) {
                    displayOneView(renderer, partialTick);
                }
                GL11.glPopMatrix();
            }
        }
        if(thisIsLast != null) {
            GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            displayOneView(thisIsLast, partialTick);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glPopMatrix();
        }

        // TODO: laser rendering happens here, maybe implement at some point
    }
    private void displayOneView(HUDRendererProvider renderer, float partialTick) {
        PlayerEntity player = Minecraft.INSTANCE.player;
        double x = renderer.getX() + 0.5 - player.prevX - ((player.x - player.prevX) * partialTick);
        double y = renderer.getY() + 0.5 - player.prevY - ((player.y - player.prevY) * partialTick);
        double z = renderer.getZ() + 0.5 - player.prevZ - ((player.z - player.prevZ) * partialTick);
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(getAngle(z,x) + 90, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef((-1)*getAngle(Math.hypot(x,z),y) + 180, 1.0F, 0.0F, 0.0F);

        GL11.glTranslatef(0.0F, 0.0F, -0.4F);

        GL11.glScalef(0.01F, 0.01F, 1F);

        renderer.getRenderer().renderHeadUpDisplay(Math.hypot(x,Math.hypot(y, z)), false, Minecraft.INSTANCE);
    }

    private float getAngle(double x, double y) {
        return (float) (Math.atan2(x,y) * 360 / (2 * Math.PI));
    }

    private int[] getCursor(HUDRendererProvider renderer) {
        PlayerEntity player = Minecraft.INSTANCE.player;

        MathVector playerView = MathVector.getFromAngles((270 - player.yaw) / 360 * -2 * Math.PI, (player.pitch) / 360 * -2 * Math.PI);
        MathVector playerPos = new MathVector();
        playerPos.X = player.x;
        playerPos.Y = player.y;
        playerPos.Z = player.z;

        MathVector panelPos = new MathVector();
        panelPos.X = renderer.getX() + 0.5;
        panelPos.Y = renderer.getY() + 0.5;
        panelPos.Z = renderer.getZ() + 0.5;

        MathVector panelView = new MathVector();
        panelView.X = playerPos.X - panelPos.X;
        panelView.Y = playerPos.Y - panelPos.Y;
        panelView.Z = playerPos.Z - panelPos.Z;

        panelPos.add(panelView, 0.44D);

        double d = panelPos.X * panelView.X + panelPos.Y * panelView.Y + panelPos.Z * panelView.Z;
        double c = panelView.X * playerPos.X + panelView.Y * playerPos.Y + panelView.Z * playerPos.Z;
        double b = panelView.X * playerView.X + panelView.Y * playerView.Y + panelView.Z * playerView.Z;
        double a = (d - c) / b;

        MathVector viewPos = new MathVector();
        viewPos.X = playerPos.X + a * playerView.X - panelPos.X;
        viewPos.Y = playerPos.Y + a * playerView.Y - panelPos.Y;
        viewPos.Z = playerPos.Z + a * playerView.Z - panelPos.Z;

        MathVector panelScalVector1 = new MathVector();

        if(panelView.Y == 0) {
            panelScalVector1.X = 0;
            panelScalVector1.Y = 1;
            panelScalVector1.Z = 0;
        } else {
            panelScalVector1 = panelView.getOrtogonal(-panelView.X, null, -panelView.Z).makeVectorLength(1.0D);
        }

        MathVector panelScalVector2 = new MathVector();

        if(panelView.Z == 0) {
            panelScalVector2.X = 0;
            panelScalVector2.Y = 0;
            panelScalVector2.Z = 1;
        } else {
            panelScalVector2 = panelView.getOrtogonal(1.0D, 0.0D, null).makeVectorLength(1.0D);
        }

        if(panelScalVector1.Y == 0) {
            return new int[]{};
        }

        double cursorY = -viewPos.Y / panelScalVector1.Y;

        MathVector restViewPos = viewPos.clone();
        restViewPos.X += cursorY*panelScalVector1.X;
        restViewPos.Y = 0;
        restViewPos.Z += cursorY*panelScalVector1.Z;

        double cursorX;

        if(panelScalVector2.X == 0) {
            cursorX = restViewPos.Z / panelScalVector2.Z;
        } else {
            cursorX = restViewPos.X / panelScalVector2.X;
        }

        cursorX *= 50 / 0.47D;
        cursorY *= 50 / 0.47D;
        if(panelView.Z < 0) {
            cursorX *= -1;
        }
        if(panelView.Y < 0) {
            cursorY *= -1;
        }

        return new int[]{(int) cursorX, (int)cursorY};
    }
}
