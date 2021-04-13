/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package com.camellias.gulliverreborn.core.helper;

import org.objectweb.asm.tree.ClassNode;

public interface SubClassTransformer {

    void transformClassNode(ClassNode cn, String transformedClassName, String obfName);

    String getIdentifier();

    void addErrorInformation();

    boolean isTransformRequired(String transformedClassName);

}
