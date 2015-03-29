/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 The Voxel Plugineering Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.voxelplugineering.voxelsniper.service.logging;

import com.voxelplugineering.voxelsniper.api.logging.LogLevel;
import com.voxelplugineering.voxelsniper.api.logging.Logger;

/**
 * A logging proxy for slf4j loggers.
 */
public class Slf4jLogger implements Logger
{

    private final org.slf4j.Logger logger;

    /**
     * Creates a new {@link Slf4jLogger}.
     * 
     * @param logger The logger to wrap
     */
    public Slf4jLogger(org.slf4j.Logger logger)
    {
        this.logger = logger;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LogLevel getLevel()
    {
        // TODO slf4j level ?
        return LogLevel.INFO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLevel(LogLevel level)
    {
        // TODO slf4j level ?
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void log(LogLevel level, String msg)
    {
        switch(level)
        {
        case OFF:
            return;
        case DEBUG:
            debug(msg);
            return;
        case WARN:
            warn(msg);
            return;
        case ERROR:
            error(msg);
            return;
        default:
            info(msg);
            return;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void debug(String msg)
    {
        this.logger.debug(msg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void info(String msg)
    {
        this.logger.info(msg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warn(String msg)
    {
        this.logger.warn(msg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(String msg)
    {
        this.logger.error(msg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(Exception e)
    {
        this.logger.error(e.getMessage(), e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(Exception e, String msg)
    {
        this.logger.error(msg, e);
    }

}
