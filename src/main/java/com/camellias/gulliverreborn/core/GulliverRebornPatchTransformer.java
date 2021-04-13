/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package com.camellias.gulliverreborn.core;

import com.camellias.gulliverreborn.core.helper.ASMTransformationException;
import com.camellias.gulliverreborn.core.helper.ClassPatch;
import com.camellias.gulliverreborn.core.helper.SubClassTransformer;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GulliverRebornPatchTransformer implements SubClassTransformer {

    private static final String PATCH_PACKAGE = "com.camellias.gulliverreborn.core.patches";

    private static ClassPatch currentPatch = null;

    private final Map<String, List<ClassPatch>> availablePatches = new HashMap<>();

    public GulliverRebornPatchTransformer() throws IOException {
        GulliverRebornCore.log.info("[GulliverRebornCore] Loading patches...");
        int loaded = loadClassPatches();
        GulliverRebornCore.log.info("[GulliverRebornCore] Initialized! Loaded " + loaded + " class patches!");
    }

    @SuppressWarnings({"UnstableApiUsage", "rawtypes"})
    private int loadClassPatches() throws IOException {
        ImmutableSet<ClassPath.ClassInfo> classes =
                ClassPath.from(Thread.currentThread().getContextClassLoader()).getTopLevelClassesRecursive(PATCH_PACKAGE);
        List<Class> patchClasses = new LinkedList<>();
        for (ClassPath.ClassInfo info : classes) {
            if(info.getName().startsWith(PATCH_PACKAGE)) {
                patchClasses.add(info.load());
            }
        }
        int load = 0;
        for (Class patchClass : patchClasses) {
            if (ClassPatch.class.isAssignableFrom(patchClass) && !Modifier.isAbstract(patchClass.getModifiers())) {
                try {
                    ClassPatch patch = (ClassPatch) patchClass.newInstance();
                    if(!availablePatches.containsKey(patch.getClassName())) {
                        availablePatches.put(patch.getClassName(), new LinkedList<>());
                    }
                    availablePatches.get(patch.getClassName()).add(patch);
                    load++;
                } catch (Exception exc) {
                    throw new IllegalStateException("Could not load ClassPatch: " + patchClass.getSimpleName(), exc);
                }
            }
        }
        if(load == 0) {
            GulliverRebornCore.log.info("[GulliverRebornCore] Found 0 Transformers! Trying to recover with direct references...");
            String[] references = new String[] {
                    "com.camellias.gulliverreborn.core.patches.PatchEntityRendererExtendedEntityReach",
                    "com.camellias.gulliverreborn.core.patches.PatchServerExtendEntityInteractReach"
            };
            for (String str : references) {
                try {
                    ClassPatch c = (ClassPatch) Class.forName(str).newInstance();
                    if(!availablePatches.containsKey(c.getClassName())) {
                        availablePatches.put(c.getClassName(), new LinkedList<>());
                    }
                    availablePatches.get(c.getClassName()).add(c);
                    load++;
                } catch (Exception exc) {
                    GulliverRebornCore.log.warn("Could not load ClassPatch: " + str);
                    exc.printStackTrace();
                }
            }
        }

        return load;
    }

    @Override
    public void transformClassNode(ClassNode cn, String transformedClassName, String obfName) {
        if(!availablePatches.isEmpty()) {
            List<ClassPatch> patches = availablePatches.get(transformedClassName);
            if(patches != null && !patches.isEmpty()) {
                GulliverRebornCore.log.info("[GulliverRebornCore] Transforming " + obfName + " : " + transformedClassName + " with " + patches.size() + " patches!");
                try {
                    for (ClassPatch patch : patches) {
                        if (!patch.canExecuteForSide(GulliverRebornCore.side)) {
                            GulliverRebornCore.log.info("[GulliverRebornCore] Skipping " + patch.getClass().getSimpleName().toUpperCase() + " as it can't be applied for side " + GulliverRebornCore.side);
                            continue;
                        }
                        currentPatch = patch;
                        patch.transform(cn);
                        GulliverRebornCore.log.info("[GulliverRebornCore] Applied patch " + patch.getClass().getSimpleName().toUpperCase());
                        currentPatch = null;
                    }
                } catch (Exception exc) {
                    throw new ASMTransformationException("Applying ClassPatches failed (ClassName: " + obfName + " - " + transformedClassName + ") - Rethrowing exception!", exc);
                }
            }
        }
    }

    @Override
    public String getIdentifier() {
        return "Patch based transformer";
    }

    @Override
    public void addErrorInformation() {
        if(currentPatch != null) {
            GulliverRebornCore.log.warn("Patcher was in active patch: " + currentPatch.getClass().getSimpleName());
        }
    }

    @Override
    public boolean isTransformRequired(String transformedClassName) {
        return availablePatches.containsKey(transformedClassName);
    }

}
