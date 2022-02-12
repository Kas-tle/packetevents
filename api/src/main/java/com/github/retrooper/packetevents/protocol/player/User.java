/*
 * This file is part of packetevents - https://github.com/retrooper/packetevents
 * Copyright (C) 2021 retrooper and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.retrooper.packetevents.protocol.player;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.netty.buffer.ByteBufAbstract;
import com.github.retrooper.packetevents.netty.channel.ChannelAbstract;
import com.github.retrooper.packetevents.protocol.ConnectionState;
import com.github.retrooper.packetevents.protocol.chat.ChatPosition;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatMessage;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class User {
    private final ChannelAbstract channel;
    private ConnectionState connectionState;
    private ClientVersion clientVersion;
    private final UserProfile profile;
    private int minWorldHeight = 0;
    private int totalWorldHeight = 256;
    private List<NBTCompound> worldNBT;

    public User(ChannelAbstract channel,
                ConnectionState connectionState, ClientVersion clientVersion,
                UserProfile profile) {
        this.channel = channel;
        this.connectionState = connectionState;
        this.clientVersion = clientVersion;
        this.profile = profile;
    }

    public ChannelAbstract getChannel() {
        return channel;
    }

    public ConnectionState getConnectionState() {
        return connectionState;
    }

    public void setConnectionState(ConnectionState connectionState) {
        this.connectionState = connectionState;
    }

    public ClientVersion getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(ClientVersion clientVersion) {
        this.clientVersion = clientVersion;
    }

    public UserProfile getProfile() {
        return profile;
    }

    public void sendPacket(ByteBufAbstract buffer) {
        PacketEvents.getAPI().getPlayerManager().sendPacket(channel, buffer);
    }

    public void sendPacket(PacketWrapper<?> wrapper) {
        PacketEvents.getAPI().getPlayerManager().sendPacket(channel, wrapper);
    }

    public void chat(String message) {
        //Fake an incoming chat packet
        WrapperPlayClientChatMessage chatMessage = new WrapperPlayClientChatMessage(message);
        PacketEvents.getAPI().getServerManager().receivePacket(channel, chatMessage);
    }

    //TODO Support colors
    public void sendMessage(String message) {
        Component component = Component.text(message);
        sendMessage(component);
    }

    public void sendMessage(Component component) {
        sendMessage(component, ChatPosition.CHAT);
    }

    public void sendMessage(Component component, ChatPosition position) {
        WrapperPlayServerChatMessage chatMessage = new WrapperPlayServerChatMessage(component, position);
        PacketEvents.getAPI().getPlayerManager().sendPacket(channel, chatMessage);
    }

    //TODO sendTitle that is cross-version

    public int getMinWorldHeight() {
        return minWorldHeight;
    }

    public void setMinWorldHeight(int minWorldHeight) {
        this.minWorldHeight = minWorldHeight;
    }

    public int getTotalWorldHeight() {
        return totalWorldHeight;
    }

    public void setTotalWorldHeight(int totalWorldHeight) {
        this.totalWorldHeight = totalWorldHeight;
    }

    public void setWorldNBT(NBTList<NBTCompound> worldNBT) {
        this.worldNBT = worldNBT.getTags();
    }

    @Nullable
    public NBTCompound getWorldNBT(String worldName) {
        for (NBTCompound compound : worldNBT) {
            if (compound.getStringTagOrNull("name").getValue().equals(worldName)) {
                return compound;
            }
        }
        return null;
    }
}
