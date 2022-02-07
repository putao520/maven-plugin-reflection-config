package putao520;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.function.Function;


@Mojo(name = "reflection",
        defaultPhase = LifecyclePhase.PACKAGE
)
@Execute(goal = "reflection",
        phase = LifecyclePhase.PACKAGE
)
public class Reflection extends AbstractMojo {
    @Parameter(name = "files", defaultValue = "")
    private String[] files = new String[0];

    // 资源目录默认路径
    @Parameter(property = "resourcesDir", defaultValue = "")
    private String resourcesDir;

    @Parameter(property = "basedir", defaultValue = "")
    private String projectFolder;

    @Parameter(property = "groupId", defaultValue = "")
    private String groupId;

    @Parameter(property = "artifactId", defaultValue = "")
    private String artifactId;


    private void dir(File dir, GeneratorGraalvmReflection gen, Function<File, Boolean> fn) {
        File[] fileList = dir.listFiles();
        for (File file : fileList) {
            if (file.isDirectory()) {
                this.dir(file, gen, fn);
            } else {
                if ((fn != null && fn.apply(file)) || fn == null) {
                    gen.put(file.getAbsolutePath());
                }
            }
        }
    }

    private void file(File f, GeneratorGraalvmReflection gen) {
        // prefix *
        String fileName = f.getName();
        if (fileName.startsWith("*")) {
            String text = fileName.substring(1);
            dir(f.getParentFile(), gen, _file -> _file.getName().endsWith(text));
        } else if (fileName.endsWith("*")) {
            String text = fileName.substring(0, fileName.length() - 1);
            dir(f.getParentFile(), gen, _file -> _file.getName().startsWith(text));
        } else {
            gen.put(f.getAbsolutePath());
        }
    }

    public void execute() {
        GeneratorGraalvmReflection gen = GeneratorGraalvmReflection.build(resourcesDir
                + File.separatorChar
                + "META-INF"
                + File.separatorChar
                + "native-image"
                + File.separatorChar
                + groupId
                + File.separatorChar
                + artifactId
                + File.separatorChar
        );
        for (String file : files) {
            File dir = new File(file);
            if (dir.isDirectory()) {
                dir(dir, gen, null);
            } else {
                file(dir, gen);
            }
        }
        gen.run();
    }
}
