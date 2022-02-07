package putao520;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GeneratorGraalvmReflection {
    private List<String> files = new ArrayList<>();
    private String currentProjectFolder;

    private GeneratorGraalvmReflection(String currentProjectFolder) {
        System.out.println("current project basedir:" + currentProjectFolder);
        this.currentProjectFolder = currentProjectFolder;
    }

    public static GeneratorGraalvmReflection build(String currentProjectFolder) {
        return new GeneratorGraalvmReflection(currentProjectFolder);
    }

    public GeneratorGraalvmReflection put(String file) {
        System.out.println("add file:" + file);
        files.add(file);
        return this;
    }

    public boolean run() {
        if (files.isEmpty()) {
            return false;
        }
        String absTempFile = GeneratorGraalvmReflectionConfig();
        try {
            File file = new File(currentProjectFolder + File.separatorChar + "gsc-reflect-config.json");
            if (file.exists()) {
                file.delete();
            }

            File temp = new File(absTempFile);
            FileUtils.copyFile(temp, file.getAbsoluteFile());
            System.out.println("Graalvm Reflection Config Path:" + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * @return 临时文件
     */
    private String GeneratorGraalvmReflectionConfig() {
        JSONArray jsonArray = new JSONArray();
        try {
            File newFile = File.createTempFile("graalvm_tmp", null);
            if (newFile.exists()) {
                newFile.delete();
            }
            newFile.createNewFile();
            String tempFileAbsolutePath = newFile.getAbsolutePath();
            for (String classFile : files) {
                JSONObject jsonObject = new JSONObject();
                ClassLoaderExpand loader = new ClassLoaderExpand(classFile);
                if (loader == null) {
                    System.out.println("Class [" + classFile + "] Not Found! 0.0.3");
                    continue;
                }
                Class<?> cls = loader.findClass();
                jsonObject.put("name", cls.getName());
                jsonObject.put("queryAllDeclaredConstructors", true);
                jsonObject.put("queryAllPublicConstructors", true);
                jsonObject.put("queryAllDeclaredMethods", true);
                jsonObject.put("queryAllPublicMethods", true);
                jsonObject.put("allDeclaredClasses", true);
                jsonObject.put("allPublicClasses", true);
                jsonArray.add(jsonObject);
            }
            FileWriter fw = new FileWriter(tempFileAbsolutePath);
            fw.write(jsonArray.toJSONString());
            fw.close();
            System.out.println("Temp Config Path:" + tempFileAbsolutePath);
            return tempFileAbsolutePath;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
