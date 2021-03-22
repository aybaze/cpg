package de.fraunhofer.aisec.cpg.frontends.golang

import de.fraunhofer.aisec.cpg.ExperimentalGolang
import de.fraunhofer.aisec.cpg.TranslationConfiguration
import de.fraunhofer.aisec.cpg.passes.scopes.ScopeManager
import de.fraunhofer.aisec.cpg.frontends.LanguageFrontend
import kotlin.Throws
import de.fraunhofer.aisec.cpg.frontends.TranslationException
import java.io.File
import de.fraunhofer.aisec.cpg.graph.declarations.TranslationUnitDeclaration
import de.fraunhofer.aisec.cpg.sarif.PhysicalLocation

class RawGolangNode {

}

@ExperimentalGolang
class GoLanguageFrontend(config: TranslationConfiguration, scopeManager: ScopeManager?) : LanguageFrontend(config, scopeManager, ".") {
    companion object {
        init {
            System.loadLibrary("cpgo")
        }
    }

    @Throws(TranslationException::class)
    override fun parse(file: File): TranslationUnitDeclaration {
        return parseInternal(file.readText(Charsets.UTF_8))
    }

    override fun <T> getCodeFromRawNode(astNode: T): String? {
        // this is handled by native code
        return null
    }

    override fun <T> getLocationFromRawNode(astNode: T): PhysicalLocation? {
        // this is handled by native code
        return null
    }

    override fun <S, T> setComment(s: S, ctx: T) {}
    
    private external fun parseInternal(s: String?): TranslationUnitDeclaration
}