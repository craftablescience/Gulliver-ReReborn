/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package com.camellias.gulliverreborn.core;

import com.camellias.gulliverreborn.core.helper.ASMTransformationException;
import com.camellias.gulliverreborn.core.helper.SubClassTransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class GulliverRebornTransformer implements IClassTransformer {

    private static final List<SubClassTransformer> subTransformers = new LinkedList<>();

    public GulliverRebornTransformer() throws IOException {
        loadSubTransformers();
    }

    private void loadSubTransformers() throws IOException {
        subTransformers.add(new GulliverRebornPatchTransformer());
    }

    private boolean isTransformationRequired(String trName) {
        for (SubClassTransformer transformer : subTransformers) {
            if(transformer.isTransformRequired(trName)) return true;
        }
        return false;
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if(!isTransformationRequired(transformedName)) return bytes;

        ClassNode node = new ClassNode();
        ClassReader reader = new ClassReader(bytes);
        reader.accept(node, 0);

        for (SubClassTransformer subTransformer : subTransformers) {
            try {
                subTransformer.transformClassNode(node, transformedName, name);
            } catch (ASMTransformationException asmException) {
                GulliverRebornCore.log.warn("Access transformation failed for Transformer: " + subTransformer.getIdentifier());
                GulliverRebornCore.log.warn("Transformer added information:");
                subTransformer.addErrorInformation();
                asmException.printStackTrace();
                throw asmException; //Rethrow
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(writer);
        return writer.toByteArray();
    }

}
