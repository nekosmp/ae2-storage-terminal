/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2014, AlgorithmX2, All rights reserved.
 *
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package appeng.server;

import java.util.Locale;

import appeng.server.subcommands.ChannelModeCommand;
import appeng.server.subcommands.ChunkLogger;
import appeng.server.subcommands.Supporters;
import appeng.server.subcommands.TickMonitoring;

public enum Commands {
    // Unrestricted
    Supporters(0, new Supporters()),

    // Admin
    Chunklogger(4, new ChunkLogger()),
    CHANNEL_MODE(4, "channelmode", new ChannelModeCommand()),
    TICK_MONITORING(4, "tickmonitor", new TickMonitoring());

    public final int level;
    public final ISubCommand command;
    public boolean test;
    public final String literal;

    Commands(int level, ISubCommand w) {
        this(level, null, w, false);
    }

    Commands(int level, String literal, ISubCommand w) {
        this(level, literal, w, false);
    }

    Commands(int level, ISubCommand w, boolean test) {
        this(level, null, w, test);
    }

    Commands(int level, String literal, ISubCommand w, boolean test) {
        this.level = level;
        this.command = w;
        this.test = test;
        this.literal = literal != null ? literal : this.name();
    }

    public String literal() {
        return literal.toLowerCase(Locale.ROOT);
    }

}
