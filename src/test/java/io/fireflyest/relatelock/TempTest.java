package io.fireflyest.relatelock;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;
// import org.objectweb.asm.ClassReader;
// import org.objectweb.asm.ClassVisitor;
// import org.objectweb.asm.ClassWriter;
// import org.objectweb.asm.FieldVisitor;
// import org.objectweb.asm.MethodVisitor;
// import org.objectweb.asm.Opcodes;
import org.yaml.snakeyaml.Yaml;

// import io.fireflyest.emberlib.config.annotation.Entry;
// import io.fireflyest.emberlib.util.ReflectionUtils;
// import io.fireflyest.emberlib.util.YamlUtils;
import io.fireflyest.relatelock.config.Lang;
import io.fireflyest.relatelock.util.TextUtils;

/**
 * 临时测试
 * 
 * @author Fireflyest
 * @since 1.0
 */
public class TempTest {
    
    @Test
    public void matchTest() {
        final FileConfiguration yamlFile = new YamlConfiguration();
        yamlFile.set("test.string", "yamlFile");
        yamlFile.set("test.int", 666);
        yamlFile.set("an.int", 999);
        // for (Field field : Lang.class.getDeclaredFields()) {
            // final Entry entry = field.getAnnotation(Entry.class);
            // if (entry != null) {
                // try {
                //     final Field modifiersField = Field.class.getDeclaredField("modifiers");
                //     ReflectionUtils.makeAccessible(modifiersField);
                //     modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL); //②

                //     // ReflectionUtils.setField(modifiersField, 
                //     //                         field, 
                //     //                         field.getModifiers() & ~Modifier.FINAL);
                // } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                //     e.printStackTrace();
                // }
                
                // final String key = "".equals(entry.value()) ? defaultKey(field) : entry.value();
                // ReflectionUtils.makeAccessible(field);
                // ReflectionUtils.setField(field, null, yamlFile.get(key));
            // }
        // }

        // System.out.println(Lang.AN_INT);
    }

    /**
     * 获取变量的默认生成键
     * 
     * @param variableElement 变量元素
     * @return 默认生成键
     */
    private static String defaultKey(Field field) {
        return symbolSplit(
            field.getName().toLowerCase(), "."
        );
    }

    public static String symbolSplit(String str, @Nonnull String delimiter) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        final Matcher wordMatcher = Pattern.compile("[a-zA-Z0-9]+").matcher(str);
        final StringBuilder stringBuilder = new StringBuilder();
        while (wordMatcher.find()) {
            stringBuilder.append(delimiter)
                            .append(wordMatcher.group());
        }
        return StringUtils.removeStart(stringBuilder.toString(), delimiter);
    }

    // @Test
    // public void testASM() throws IOException {

    //     System.err.println(Lang.AN_INT);

    //     final ClassWriter cw = new ClassWriter(0);
    //     // cv forwards all events to cw
    //     final ClassVisitor cv = new ClassVisitor(Opcodes.ASM4, cw) {

    //         @Override
    //         public void visit(int version, int access, String name,
    //                 String signature, String superName, String[] interfaces) {
    //             cv.visit(Opcodes.V1_5, access, name, signature, superName, interfaces);
    //         }

    //         @Override
    //         public MethodVisitor visitMethod(int access, String name,
    //                 String desc, String signature, String[] exceptions) {
    //             System.out.println(name);
    //             System.out.println(desc);
    //             if (name.equals("AN_INT") && desc.equals("(Ljava/lang/Object;)I")) {
    //                 // do not delegate to next visitor-> this removes the method
    //                 return null;
    //             }
    //             return cv.visitMethod(access, name, desc, signature, exceptions);
    //         }

    //     };
    //     final ClassReader cr = new ClassReader(Lang.class.getName());
    //     cr.accept(cv, 0);
    //     final byte[] b2 = cw.toByteArray();

    //     Class c = new MyClassLoader().defineClass("io/fireflyest/relatelock/config", b2);

    //     System.err.println(Lang.AN_INT);

    // }

    // class MyClassLoader extends ClassLoader {
    //     public Class defineClass(String name, byte[] b) {
    //         return defineClass(name, b, 0, b.length);
    //     }
    // }

}
