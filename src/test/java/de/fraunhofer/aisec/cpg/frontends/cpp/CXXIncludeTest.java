/*
 * Copyright (c) 2019, Fraunhofer AISEC. All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *                    $$$$$$\  $$$$$$$\   $$$$$$\
 *                   $$  __$$\ $$  __$$\ $$  __$$\
 *                   $$ /  \__|$$ |  $$ |$$ /  \__|
 *                   $$ |      $$$$$$$  |$$ |$$$$\
 *                   $$ |      $$  ____/ $$ |\_$$ |
 *                   $$ |  $$\ $$ |      $$ |  $$ |
 *                   \$$$$$   |$$ |      \$$$$$   |
 *                    \______/ \__|       \______/
 *
 */

package de.fraunhofer.aisec.cpg.frontends.cpp;

import static org.junit.jupiter.api.Assertions.*;

import de.fraunhofer.aisec.cpg.BaseTest;
import de.fraunhofer.aisec.cpg.TestUtils;
import de.fraunhofer.aisec.cpg.graph.*;
import de.fraunhofer.aisec.cpg.sarif.PhysicalLocation;
import de.fraunhofer.aisec.cpg.sarif.Region;
import java.io.File;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CXXIncludeTest extends BaseTest {

  @Test
  void testDefinitionsAndDeclaration() throws Exception {
    File file = new File("src/test/resources/include.cpp");
    TranslationUnitDeclaration tu =
        TestUtils.analyzeAndGetFirstTU(List.of(file), file.getParentFile().toPath(), true);
    for (Declaration d : tu.getDeclarations()) {
      System.out.println(d.getName() + " " + d.getLocation());
    }
    assertEquals(5, tu.getDeclarations().size());

    RecordDeclaration someClass =
        tu.getDeclarationsByName("SomeClass", RecordDeclaration.class).iterator().next();
    assertNotNull(someClass);

    Set<FunctionDeclaration> main = tu.getDeclarationsByName("main", FunctionDeclaration.class);
    assertFalse(main.isEmpty());

    ConstructorDeclaration someClassConstructor =
        tu.getDeclarationsByName("SomeClass", ConstructorDeclaration.class).iterator().next();
    assertNotNull(someClassConstructor);
    assertEquals(someClass, someClassConstructor.getRecordDeclaration());

    MethodDeclaration doSomething =
        tu.getDeclarationsByName("DoSomething", MethodDeclaration.class).iterator().next();
    assertNotNull(doSomething);
    assertEquals(someClass, doSomething.getRecordDeclaration());

    ReturnStatement returnStatement = doSomething.getBodyStatementAs(0, ReturnStatement.class);
    assertNotNull(returnStatement);

    DeclaredReferenceExpression ref =
        (DeclaredReferenceExpression) returnStatement.getReturnValue();
    assertNotNull(ref);

    FieldDeclaration someField = someClass.getField("someField");
    assertNotNull(someField);

    assertTrue(ref.getRefersTo().contains(someField));
  }

  @Test
  void testCodeAndRegionInInclude() throws Exception {
    // checks, whether code and region for nodes in includes are properly set
    File file = new File("src/test/resources/include.cpp");
    TranslationUnitDeclaration tu =
        TestUtils.analyzeAndGetFirstTU(List.of(file), file.getParentFile().toPath(), true);

    Set<RecordDeclaration> someClass =
        tu.getDeclarationsByName("SomeClass", RecordDeclaration.class);
    assertFalse(someClass.isEmpty());

    ConstructorDeclaration decl = someClass.iterator().next().getConstructors().get(0);
    assertEquals("SomeClass();", decl.getCode());

    PhysicalLocation location = decl.getLocation();
    assertNotNull(location);

    assertEquals(new Region(16, 3, 16, 15), location.getRegion());
  }
}
