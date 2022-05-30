package io.wispforest.idwtialsimmoedm.cloth;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.wispforest.idwtialsimmoedm.IdwtialsimmoedmConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.lang.reflect.Field;
import java.util.function.Consumer;

public class IdwtialsimmoedmModMenuHook implements ModMenuApi {

    private static final IdwtialsimmoedmConfig DEFAULT_VALUES = new IdwtialsimmoedmConfig();

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            final var builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(new TranslatableText("text.idwtialsimmoedm.config.title"));

            final var entryBuilder = builder.entryBuilder();
            final var configInstance = IdwtialsimmoedmConfig.get();
            final var category = builder.getOrCreateCategory(Text.of("category moment"));

            for (var field : IdwtialsimmoedmConfig.class.getFields()) {
                if (field.getType() == boolean.class) {
                    category.addEntry(entryBuilder.startBooleanToggle(fieldName(field), fieldGet(configInstance, field))
                            .setSaveConsumer(fieldSetter(configInstance, field))
                            .setDefaultValue((boolean) fieldGet(DEFAULT_VALUES, field)).build());
                } else if (field.getType() == String.class) {
                    category.addEntry(entryBuilder.startStrField(fieldName(field), fieldGet(configInstance, field))
                            .setSaveConsumer(fieldSetter(configInstance, field))
                            .setDefaultValue((String) fieldGet(DEFAULT_VALUES, field)).build());
                }
            }

            builder.setSavingRunnable(IdwtialsimmoedmConfig::save);
            return builder.build();
        };
    }

    private static Text fieldName(Field field)  {
        return new TranslatableText("text.idwtialsimmoedm.config.field." + field.getName());
    }

    @SuppressWarnings("unchecked")
    private static <T> T fieldGet(Object instance, Field field) {
        try {
            return (T) field.get(instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> Consumer<T> fieldSetter(Object instance, Field field) {
        return t -> {
            try {
                field.set(instance, t);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
