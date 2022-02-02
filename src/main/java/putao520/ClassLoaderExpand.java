package putao520;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClassLoaderExpand extends ClassLoader {
    // 指定文件目录
    private String location;
    private String name;


    public ClassLoaderExpand() {
        location = null;
        name = "";
    }

    public ClassLoaderExpand(String fullPath) {
        File file = new File(fullPath);
        if (file.isDirectory()) {
            location = file.getAbsolutePath();
            name = "";
        } else {
            location = file.getParent();
            name = file.getName().split("\\.")[0];
        }
    }

    protected Class<?> findClass() {
        return findClass(name);
    }

    /**
     * name class 类的文件名
     */
    @Override
    protected Class<?> findClass(String name) {
        name = name.split("\\.")[0];
        byte[] datas = loadClassData(name);
        String prefix = location;
        String[] prefixArr = prefix.indexOf("/") >= 0 ? prefix.split("/") : prefix.split("\\\\");
        List<String> nameFullArr = new ArrayList<>();
        nameFullArr.add(name);
        int i = prefixArr.length;
        do {
            try {
                String _name = "";
                for (int l = nameFullArr.size(); l > 0; l--) {
                    _name += (nameFullArr.get(l - 1) + ".");
                }
                if (_name.endsWith(".")) {
                    _name = _name.substring(0, _name.length() - 1);
                }
                return defineClass(_name, datas, 0, datas.length);
            } catch (NoClassDefFoundError e1) {
                i--;
                nameFullArr.add(prefixArr[i]);
            } catch (Exception e) {
                i--;
                nameFullArr.add(prefixArr[i]);
            }
        } while (i > 0);
        return null;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    protected byte[] loadClassData(String name) {
        FileInputStream fis = null;
        byte[] datas = null;
        String ch = location.indexOf("/") >= 0 ? "/" : "\\\\";
        try {
            fis = new FileInputStream(location + ch + name + ".class");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int b;
            while ((b = fis.read()) != -1) {
                bos.write(b);
            }
            datas = bos.toByteArray();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null)
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return datas;

    }
}
