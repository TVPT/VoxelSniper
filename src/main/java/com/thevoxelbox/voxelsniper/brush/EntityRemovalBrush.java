package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class EntityRemovalBrush extends Brush
{
    private final List<Class<?>> exemptions = new ArrayList<Class<?>>(3);

    /**
     *
     */
    public EntityRemovalBrush()
    {
        this.setName("Entity Removal");

        exemptions.add(Player.class);
        exemptions.add(Hanging.class);
        exemptions.add(NPC.class);
    }

    private void radialRemoval(SnipeData v)
    {
        final Chunk targetChunk = getTargetBlock().getChunk();
        int entityCount = 0;
        int chunkCount = 0;

        entityCount += removeEntities(targetChunk);

        int radius = Math.round(v.getBrushSize() / 16);

        for (int x = targetChunk.getX() - radius; x <= targetChunk.getX() + radius; x++)
        {
            for (int z = targetChunk.getZ() - radius; z <= targetChunk.getZ() + radius; z++)
            {
                entityCount += removeEntities(getWorld().getChunkAt(x, z));
                chunkCount++;
            }
        }
        v.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.RED + entityCount + ChatColor.GREEN + " entities out of " + ChatColor.BLUE + chunkCount + ChatColor.GREEN + " chunks.");
    }

    private int removeEntities(Chunk chunk)
    {
        int entityCount = 0;

        for (Entity entity : chunk.getEntities())
        {
            if (isClassInExemptionList(entity.getClass()))
            {
                continue;
            }

            entity.remove();
            entityCount++;
        }

        return entityCount;
    }

    private boolean isClassInExemptionList(Class<? extends Entity> entityClass)
    {
        for (final Class<?> type : exemptions)
        {
            if (type.isAssignableFrom(entityClass))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void arrow(SnipeData v)
    {
        this.radialRemoval(v);
    }

    @Override
    protected void powder(SnipeData v)
    {
        this.radialRemoval(v);
    }

    @Override
    public void info(Message vm)
    {
        vm.brushName(getName());

        final StringBuilder exemptionsList = new StringBuilder(ChatColor.GREEN + "Exemptions: " + ChatColor.LIGHT_PURPLE);
        for (Iterator it = exemptions.iterator(); it.hasNext(); )
        {
            exemptionsList.append(prettyPrintClassName((Class<?>) it.next(), false));
            if (it.hasNext())
            {
                exemptionsList.append(", ");
            }
        }
        vm.custom(exemptionsList.toString());

        vm.size();
    }

    @Override
    public void parameters(final String[] par, final SnipeData v)
    {
        for (int i = 0; i < par.length; ++i)
        {
            final String currentParam = par[i];

            if (currentParam.equalsIgnoreCase("exemptions") || currentParam.equalsIgnoreCase("ex"))
            {
                for (int k = ++i; k < par.length; ++k, ++i)
                {
                    final String currentExemption = par[k];

                    if (!currentExemption.startsWith("+") && !currentExemption.startsWith("-"))
                    {
                        break;
                    }

                    final boolean isAddOperation = currentExemption.startsWith("+");
                    final String exemptionClassName = currentExemption.substring(1);

                    // Try turning exemptionClassName into an actual class
                    // Prefix with org.bukkit.entity, then without prefix if no matching class was found
                    Class<?> exemptionClass = tryConvertStringToClass("org.bukkit.entity." + exemptionClassName);
                    if (exemptionClass == null)
                    {
                        exemptionClass = tryConvertStringToClass(exemptionClassName);
                    }

                    if (exemptionClass == null)
                    {
                        v.sendMessage(String.format("Could not convert %s into a class.", exemptionClassName));
                        continue;
                    }

                    if (!Entity.class.isAssignableFrom(exemptionClass))
                    {
                        v.sendMessage(String.format(
                                "%s is not assignable from %s",
                                prettyPrintClassName(Entity.class, true),
                                prettyPrintClassName(exemptionClass, true)
                        ));
                        continue;
                    }

                    if (isAddOperation)
                    {
                        exemptions.add(exemptionClass);
                        v.sendMessage(String.format("Added %s to entity exemptions list.", exemptionClassName));
                    }
                    else
                    {
                        exemptions.remove(exemptionClass);
                        v.sendMessage(String.format("Removed %s to entity from exemptions list.", exemptionClassName));
                    }
                }
            }
            else if (currentParam.equalsIgnoreCase("list-exemptions") || currentParam.equalsIgnoreCase("lex"))
            {
                for (final Class<?> exemption : exemptions)
                {
                    v.sendMessage(ChatColor.LIGHT_PURPLE + prettyPrintClassName(exemption, true));
                }
            }
        }
    }

    private String prettyPrintClassName(Class<?> clazz, boolean fullyQualifiedName)
    {
        return fullyQualifiedName ? clazz.getName() : clazz.getSimpleName();
    }

    private Class<?> tryConvertStringToClass(String qualifiedClassName)
    {
        try
        {
            return Class.forName(qualifiedClassName);
        }
        catch (final ClassNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getPermissionNode()
    {
        return "voxelsniper.brush.entityremoval";
    }
}
