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
package com.voxelplugineering.voxelsniper.forge.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.voxelplugineering.voxelsniper.api.registry.MaterialRegistry;
import com.voxelplugineering.voxelsniper.api.registry.RegistryProvider;
import com.voxelplugineering.voxelsniper.api.world.material.Material;
import com.voxelplugineering.voxelsniper.core.Gunsmith;
import com.voxelplugineering.voxelsniper.core.registry.ProvidedWeakRegistry;
import com.voxelplugineering.voxelsniper.core.service.AbstractService;

/**
 * A standard material registry for materials.
 * 
 * @param <T> The underlying material type
 */
public class ProvidedMaterialRegistryService<T> extends AbstractService implements MaterialRegistry<T>
{

    private ProvidedWeakRegistry<T, Material> registry;
    private final RegistryProvider<T, Material> provider;
    private String defaultMaterialName;

    /**
     * Creates a new {@link ProvidedMaterialRegistryService} with the given provider.
     * 
     * @param provider The material provider
     */
    public ProvidedMaterialRegistryService(RegistryProvider<T, Material> provider)
    {
        super(MaterialRegistry.class, 5);
        this.provider = provider;
    }

    @Override
    public String getName()
    {
        return "materialRegistry";
    }

    @Override
    protected void init()
    {
        this.registry = new ProvidedWeakRegistry<T, Material>(this.provider);
        this.registry.setCaseSensitiveKeys(false);
        this.defaultMaterialName = Gunsmith.getConfiguration().get("defaultMaterialName", String.class).or("air");
        Gunsmith.getLogger().info("Initialized MaterialRegistry service");
    }

    @Override
    protected void destroy()
    {
        this.registry = null;
        this.defaultMaterialName = null;
        Gunsmith.getLogger().info("Stopped MaterialRegistry service");
    }

    @Override
    public Material getAirMaterial()
    {
        return getMaterial(this.defaultMaterialName).get();
    }

    @Override
    public Optional<Material> getMaterial(String name)
    {
        check();
        return this.registry.get(name);
    }

    @Override
    public Optional<Material> getMaterial(T material)
    {
        check();
        return this.registry.get(material);
    }

    @Override
    public void registerMaterial(String name, T object, Material material)
    {
        //TODO clean up the registry to remove this method
        throw new UnsupportedOperationException("Cannot register with a provided registry");
    }

    @Override
    public Collection<Material> getMaterials()
    {
        check();
        Set<Map.Entry<T, Material>> entries = this.registry.getRegisteredValues();
        List<Material> mats = Lists.newArrayList();
        for (Map.Entry<T, Material> entry : entries)
        {
            mats.add(entry.getValue());
        }
        return mats;
    }

}
