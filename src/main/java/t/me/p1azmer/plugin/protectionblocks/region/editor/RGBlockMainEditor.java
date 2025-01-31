package t.me.p1azmer.plugin.protectionblocks.region.editor;

import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.menu.impl.EditorMenu;
import t.me.p1azmer.engine.api.menu.impl.MenuViewer;
import t.me.p1azmer.engine.editor.EditorManager;
import t.me.p1azmer.engine.utils.*;
import t.me.p1azmer.engine.utils.collections.Lists;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionblocks.api.currency.Currency;
import t.me.p1azmer.plugin.protectionblocks.config.Config;
import t.me.p1azmer.plugin.protectionblocks.config.Lang;
import t.me.p1azmer.plugin.protectionblocks.editor.EditorLocales;
import t.me.p1azmer.plugin.protectionblocks.region.editor.breakers.RGBlockBreakersListEditor;
import t.me.p1azmer.plugin.protectionblocks.region.editor.recipe.RGBlockRecipeEditor;
import t.me.p1azmer.plugin.protectionblocks.region.impl.block.RegionBlock;

import java.util.ArrayList;
import java.util.List;

public class RGBlockMainEditor extends EditorMenu<ProtectionPlugin, RegionBlock> {
    private static final ItemStack WORLD_SKULL = ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjgwZDMyOTVkM2Q5YWJkNjI3NzZhYmNiOGRhNzU2ZjI5OGE1NDVmZWU5NDk4YzRmNjlhMWMyYzc4NTI0YzgyNCJ9fX0=");
    private static final ItemStack DEPOSIT_SKULL = ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjg4OWNmY2JhY2JlNTk4ZThhMWNkODYxMGI0OWZjYjYyNjQ0ZThjYmE5ZDQ5MTFkMTIxMTM0NTA2ZDhlYTFiNyJ9fX0=");
    private static final ItemStack LIFE_TIME_SKULL = ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTRiZDlhNDViOTY4MWNlYTViMjhjNzBmNzVhNjk1NmIxZjU5NGZlYzg0MGI5NjA3Nzk4ZmIxZTcwNzc2NDQzMCJ9fX0=");
    private static final ItemStack GROUP_SIZE_SKULL = ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWJkYTVmMzE5MzdiMmZmNzU1MjcxZDk3ZjAxYmU4NGQ1MmE0MDdiMzZjYTc3NDUxODU2MTYyYWM2Y2ZiYjM0ZiJ9fX0=");
    private static final ItemStack STRENGTH_SKULL = ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQzNmMzMjkxZmUwMmQxNDJjNGFmMjhkZjJmNTViYjAzOTdlMTk4NTU0ZTgzNDU5OTBkYmJjZDRjMTQwMzE2YiJ9fX0=");
    private RGBlockBreakersListEditor breakersListEditor;
    private RGBlockRecipeEditor recipeEditor;

    public RGBlockMainEditor(@NotNull RegionBlock regionBlock) {
        super(regionBlock.plugin(), regionBlock, "Region Block Settings", 54);

        this.addReturn(49)
                .setClick((viewer, event) ->
                        this.plugin.getRegionManager().getEditor().openAsync(viewer.getPlayer(), 1));

        this.addItem(Material.STONE, EditorLocales.REGION_BLOCK_ITEM, 4)
                .setClick((viewer, event) -> {
                    if (event.isRightClick()) {
                        PlayerUtil.addItem(viewer.getPlayer(), regionBlock.getItem());
                        return;
                    }

                    ItemStack cursor = event.getCursor();
                    if (!cursor.getType().isAir()) {
                        regionBlock.setItem(cursor);
                        PlayerUtil.addItem(viewer.getPlayer(), cursor);
                        viewer.getPlayer().setItemOnCursor(null);
                        this.save(viewer);
                    }
                })
                .getOptions()
                .setDisplayModifier(((viewer, item) -> {
                    item.setType(regionBlock.getItem().getType());
                    item.setItemMeta(regionBlock.getItem().getItemMeta());
                    item.setAmount(regionBlock.getItem().getAmount());

                    item.setItemMeta(regionBlock.getItem().getItemMeta());

                    List<String> lore = ItemUtil.getLore(regionBlock.getItem());
                    lore.addAll(EditorLocales.REGION_BLOCK_ITEM.getLocalizedLore());

                    String displayName = Colorizer.apply(Colors2.GRAY + "(&r" + ItemUtil.getItemName(item) + Colors2.GRAY + ") " + EditorLocales.REGION_BLOCK_ITEM.getLocalizedName());
                    ItemReplacer.create(item)
                            .setDisplayName(displayName)
                            .setLore(lore)
                            .writeMeta();
                }));

        this.addItem(Material.NAME_TAG, EditorLocales.REGION_BLOCK_NAME, 10)
                .setClick((viewer, event) ->
                        this.handleInput(viewer, Lang.Editor_Region_Block_Enter_Name, wrapper -> {
                            regionBlock.setName(wrapper.getText());
                            regionBlock.save();
                            return true;
                        }));
        this.addItem(Material.MOSS_BLOCK, EditorLocales.REGION_BLOCK_SIZE, 11)
                .setClick((viewer, event) -> {
                    if (event.isLeftClick()) {
                        this.handleInput(viewer, Lang.Editor_Region_Block_Enter_Value, wrapper -> {
                            regionBlock.setRegionSize(wrapper.asInt(1));
                            regionBlock.save();
                            return true;
                        });
                    } else if (event.isRightClick()) {
                        regionBlock.setInfinityYBlocks(!regionBlock.isInfinityYBlocks());
                        this.save(viewer);
                    }
                });
        this.addItem(STRENGTH_SKULL, EditorLocales.REGION_BLOCK_STRENGTH, 12)
                .setClick((viewer, event) ->
                        this.handleInput(viewer, Lang.Editor_Region_Block_Enter_Value, wrapper -> {
                            regionBlock.setStrength(wrapper.asInt(1));
                            regionBlock.save();
                            return true;
                        }));
        this.addItem(Material.ARMOR_STAND, EditorLocales.REGION_HOLOGRAM, 14)
                .setClick((viewer, event) -> {
                    if (event.getClick().equals(ClickType.DROP)) {
                        regionBlock.setHologramInRegion(!regionBlock.isHologramInRegion());
                        regionBlock.getManager().getRegionsWithBlocks(regionBlock).forEach(regionBlock::updateHologram);
                        this.save(viewer);
                    }
                    if (event.isLeftClick()) {
                        regionBlock.setHologramEnabled(!regionBlock.isHologramEnabled());
                        regionBlock.getManager().getRegionsWithBlocks(regionBlock).forEach(regionBlock::updateHologram);
                        this.save(viewer);
                    } else if (event.isRightClick()) {
                        this.handleInput(viewer, Lang.Editor_Region_Block_Enter_Hologram_Template, wrapper -> {
                            regionBlock.setHologramTemplate(wrapper.getTextRaw());
                            regionBlock.getManager().getRegionsWithBlocks(regionBlock).forEach(regionBlock::updateHologram);
                            regionBlock.save();
                            return true;
                        });
                        EditorManager.suggestValues(viewer.getPlayer(), Config.REGION_HOLOGRAM_TEMPLATES.get().keySet(), true);
                    }
                });
        this.addItem(DEPOSIT_SKULL, EditorLocales.REGION_BLOCK_DEPOSIT, 15)
                .setClick((viewer, event) -> {
                    if (event.isLeftClick()) {
                        this.handleInput(viewer, Lang.Editor_Region_Block_Enter_Value, wrapper -> {
                            regionBlock.setDepositPrice(wrapper.asInt(1));
                            regionBlock.save();
                            return true;
                        });
                    } else if (event.isRightClick()) {
                        this.handleInput(viewer, Lang.Editor_Region_Block_Enter_Currency, wrapper -> {
                            Currency currency = this.plugin().getCurrencyManager().getCurrency(wrapper.getTextRaw());
                            if (currency == null) {
                                EditorManager.error(viewer.getPlayer(), plugin().getMessage(Lang.Editor_Region_Block_Error_Currency_NF).getLocalized());
                                return false;
                            }
                            regionBlock.setCurrencyId(currency.getId());
                            regionBlock.save();
                            return true;
                        });
                        EditorManager.suggestValues(viewer.getPlayer(), plugin().getCurrencyManager().getCurrencyIds(), true);
                    }
                });
        this.addItem(WORLD_SKULL, EditorLocales.REGION_BLOCK_WORLDS, 16)
                .setClick((viewer, event) -> {
                    if (event.isShiftClick()) {
                        if (event.isRightClick()) {
                            regionBlock.setWorlds(new ArrayList<>());
                            this.save(viewer);
                        }
                    } else {
                        if (event.isLeftClick()) {
                            EditorManager.suggestValues(viewer.getPlayer(), Lists.worldNames(), true);
                            this.handleInput(viewer, Lang.Editor_Region_Block_Enter_World, wrapper -> {
                                List<String> list = regionBlock.getWorlds();
                                list.add(wrapper.getText());
                                regionBlock.setWorlds(list);
                                regionBlock.save();
                                return true;
                            });
                        }
                    }
                });

        this.addItem(Material.IRON_PICKAXE, EditorLocales.REGION_BLOCK_BREAKERS_ICON, 21)
                .setClick((viewer, event) -> this.getEditorBreakers().openAsync(viewer.getPlayer(), 1));
        this.addItem(Material.CRAFTING_TABLE, EditorLocales.REGION_BLOCK_RECIPE_ICON, 23)
                .setClick((viewer, event) -> {
                    if (event.getClick().equals(ClickType.DROP)) {
                        regionBlock.getBlockRecipe().setEnabled(!regionBlock.getBlockRecipe().isEnabled());
                        this.save(viewer);
                        return;
                    }
                    this.getRecipeEditor().openAsync(viewer.getPlayer(), 1);
                });

        this.addItem(LIFE_TIME_SKULL, EditorLocales.REGION_BLOCK_LIFE_TIME, 30)
                .setClick((viewer, event) -> {
                    regionBlock.setLifeTimeEnabled(!regionBlock.isLifeTimeEnabled());
                    this.save(viewer);
                });
        this.addItem(GROUP_SIZE_SKULL, EditorLocales.REGION_BLOCK_GROUP_SIZE, 31)
                .setClick((viewer, event) -> {
                    regionBlock.setGroupSizeEnabled(!regionBlock.isGroupSizeEnabled());
                    this.save(viewer);
                });
        this.addItem(new ItemStack(Material.BARRIER), EditorLocales.REGION_BLOCK_PLACE_LIMIT, 32)
                .setClick((viewer, event) -> {
                    regionBlock.setPlaceLimitEnabled(!regionBlock.isPlaceLimitEnabled());
                    this.save(viewer);
                });


        this.getItems().forEach(menuItem -> {
            if (menuItem.getOptions().getDisplayModifier() == null) {
                menuItem.getOptions().setDisplayModifier(((viewer, item) -> ItemReplacer.replace(item, regionBlock.replacePlaceholders())));
            }
        });
    }

    @Override
    public void clear() {
        super.clear();
        if (this.breakersListEditor != null) {
            this.breakersListEditor.clear();
            this.breakersListEditor = null;
        }
    }

    @NotNull
    public RGBlockBreakersListEditor getEditorBreakers() {
        if (this.breakersListEditor == null) {
            this.breakersListEditor = new RGBlockBreakersListEditor(this.object);
        }
        return this.breakersListEditor;
    }

    @NotNull
    public RGBlockRecipeEditor getRecipeEditor() {
        if (this.recipeEditor == null) {
            this.recipeEditor = new RGBlockRecipeEditor(this.object);
        }
        return this.recipeEditor;
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.save();
        this.plugin.runTaskAsync(async -> this.plugin.getRegionManager().getRegions().forEach(this.object::updateHologram));
        this.openAsync(viewer.getPlayer(), viewer.getPage());
    }

    @Override
    public void onClick(@NotNull MenuViewer viewer, @Nullable ItemStack item, @NotNull SlotType slotType, int slot, @NotNull InventoryClickEvent event) {
        super.onClick(viewer, item, slotType, slot, event);
        if (slotType == SlotType.PLAYER || slotType == SlotType.PLAYER_EMPTY) {
            event.setCancelled(false);
        }
    }
}