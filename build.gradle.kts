
import com.jfrog.bintray.gradle.BintrayExtension
import com.jfrog.bintray.gradle.BintrayExtension.PackageConfig
import com.jfrog.bintray.gradle.BintrayExtension.VersionConfig
import net.minecraftforge.gradle.user.UserBaseExtension
import org.eclipse.jgit.api.Git
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*


buildscript {
    repositories {
        jcenter()
        maven {
            name = "forge"
            setUrl("http://files.minecraftforge.net/maven")
        }
    }
    dependencies {
        classpath("net.minecraftforge.gradle:ForgeGradle:2.3-SNAPSHOT")
        classpath("org.eclipse.jgit:org.eclipse.jgit:5.5.0.201909110433-r")
    }
}

plugins {
    id("com.matthewprenger.cursegradle") version "1.1.0"
    id("maven-publish")
    id("com.jfrog.bintray") version "1.8.4"
}

apply {
    plugin("net.minecraftforge.gradle.forge")
}

val config: Properties = file("build.properties").inputStream().let {
    val prop = Properties()
    prop.load(it)
    return@let prop
}

val mcVersion = config["minecraft.version"] as String
val mcFullVersion = "$mcVersion-${config["forge.version"]}"
val shortVersion = mcVersion.substring(0, mcVersion.lastIndexOf("."))
val strippedVersion = shortVersion.replace(".", "") + "0"

val forestryVersion = config["forestry.version"] as String
val chickenasmVersion = config["chickenasm.version"] as String
val cclVersion = config["ccl.version"] as String
val multipartVersion = config["multipart.version"] as String
val crafttweakerVersion = config["crafttweaker.version"] as String
val jeiVersion = config["jei.version"] as String
val topVersion = config["top.version"] as String
val ctmVersion = config["ctm.version"] as String

val git: Git = Git.open(File("."))

val modVersion = getVersionFromJava(file("src/main/java/gtr/GregTechVersion.java"))
val modVersionNoBuild = modVersion.substring(0, modVersion.lastIndexOf('.'))
version = "$mcVersion-$modVersion"
group = "gtr"

configure<BasePluginConvention> {
    archivesBaseName = "gtr"
}

fun minecraft(configure: UserBaseExtension.() -> Unit) = project.configure(configure)

minecraft {
    version = mcFullVersion
    mappings = "stable_39"
    runDir = "run"
    isUseDepAts = true
}

repositories {
    maven {
        name = "ic2, forestry"
        setUrl("http://maven.ic2.player.to/")
    }
    maven { //JEI
        name = "Progwml6 maven"
        setUrl("http://dvs1.progwml6.com/files/maven/")
    }
    maven { //JEI fallback
        name = "ModMaven"
        setUrl("modmaven.k-4u.nl")
    }
    maven {
        name = "Curse Maven"
        setUrl("https://www.cursemaven.com")
    }
    maven {
        name = "tterrag maven"
        setUrl("http://maven.tterrag.com/")
    }
    maven {
        name = "ChickenBones maven"
        setUrl("http://chickenbones.net/maven/")
    }
    maven {
        name = "CoFH Maven"
        setUrl("http://maven.covers1624.net")
    }
    //maven {
    //    name = "tehnut maven"
    //    setUrl("http://tehnut.info/maven/")
    //}
    maven {
        name = "CraftTweaker Maven"
        setUrl("https://maven.blamejared.com/")
    }
    maven {
        name = "CCL Maven New"
        setUrl("https://minecraft.curseforge.com/api/maven")
    }
    maven {
        name = "Modmaven for Applied Energistics 2"
        setUrl("https://modmaven.dev/")
    }

}

val GCBuild = "261"
val GCVersion = "1.12.2-4.0.2.${GCBuild}"

dependencies {
    "deobfCompile"("net.sengir.forestry:forestry_$mcVersion:$forestryVersion") {
        isTransitive = false
    }
    "deobfCompile"("codechicken:ChickenASM:$shortVersion-$chickenasmVersion")
    "deobfCompile"("codechicken-lib-1-8:CodeChickenLib-$mcVersion:$cclVersion:universal")
    "deobfCompile"("forge-multipart-cbe:ForgeMultipart-$mcVersion:$multipartVersion:universal")
    "deobfCompile"("CraftTweaker2:CraftTweaker2-MC$strippedVersion-Main:$crafttweakerVersion")
    "deobfCompile"("mezz.jei:jei_$mcVersion:$jeiVersion")
    "deobfCompile"("mcjty.theoneprobe:TheOneProbe-$shortVersion:$shortVersion-$topVersion")
    "deobfCompile"("team.chisel.ctm:CTM:MC$mcVersion-$ctmVersion")
    "deobfCompile"("appeng:appliedenergistics2:rv6-stable-7")
    "deobfCompile"("curse.maven:industrial-craft-242638:3078604")

}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

val processResources: ProcessResources by tasks
val sourceSets: SourceSetContainer = the<JavaPluginConvention>().sourceSets

processResources.apply {
    inputs.property("version", modVersion)
    inputs.property("mcversion", mcFullVersion)

    from(sourceSets["main"].resources.srcDirs) {
        include("mcmod.info")
        expand(mapOf("version" to modVersion,
            "mcversion" to mcFullVersion))
    }

    // copy everything else, thats not the mcmod.info
    from(sourceSets["main"].resources.srcDirs) {
        exclude("mcmod.info")
    }
    // access transformer
    rename("(.+_at.cfg)", "META-INF/$1")
}

val jar: Jar by tasks
jar.apply {
    manifest {
        attributes(mapOf("FMLAT" to "gregtech_at.cfg",
            "FMLCorePlugin" to "gtr.common.asm.GTRLoadingPlugin",
            "FMLCorePluginContainsFMLMod" to "true"))
    }
}

val sourceTask: Jar = tasks.create("source", Jar::class.java) {
    from(sourceSets["main"].allSource)
    classifier = "sources"
}

val devTask: Jar = tasks.create("dev", Jar::class.java) {
    from(sourceSets["main"].output)
    classifier = "dev"
}

val energyApiTask: Jar = tasks.create("energyApi", Jar::class.java) {
    from(sourceSets["main"].allSource)
    from(sourceSets["main"].output)

    include("gtr/api/capability/IElectricItem.*")
    include("gtr/api/capability/IEnergyContainer.*")
    include("gtr/api/capability/GregtechCapabilities.*")

    classifier = "energy-api"
}

artifacts {
    add("archives", jar)
    add("archives", sourceTask)
    add("archives", energyApiTask)
}

fun Project.idea(configure: org.gradle.plugins.ide.idea.model.IdeaModel.() -> Unit): Unit =
    (this as ExtensionAware).extensions.configure("idea", configure)
idea {
    module {
        inheritOutputDirs = true
    }
}


tasks["build"]

fun getVersionFromJava(file: File): String  {
    var major = "0"
    var minor = "0"
    var revision = "0"

    val prefix = "public static final int"
    file.forEachLine { line ->
        var s = line.trim()
        if (s.startsWith(prefix)) {
            s = s.substring(prefix.length, s.length - 1)
            s = s.replace("=", " ").replace(" +", " ").trim()
            val pts = s.split(" ")

            when {
                pts[0] == "MAJOR" -> major = pts[pts.size - 1]
                pts[0] == "MINOR" -> minor = pts[pts.size - 1]
                pts[0] == "REVISION" -> revision = pts[pts.size - 1]
            }
        }
    }

    val branchNameOrTag = System.getenv("CI_COMMIT_REF_NAME")
    if (branchNameOrTag != null && !branchNameOrTag.startsWith("v") && branchNameOrTag != "master") {
        return "$major.$minor.$revision-$branchNameOrTag"
    }


    return "$major.$minor.$revision"
}


fun BintrayExtension.pkg(config: PackageConfig.() -> Unit) = PackageConfig().also {
    it.config()
    this.pkg = it
}

fun BintrayExtension.version(config: VersionConfig.() -> Unit) {
    VersionConfig().also {
        it.config()
        this.pkg.version = it
    }
}

bintray {
    val bintrayUser = if (project.hasProperty("bintrayUser")) project.property("bintrayUser") as String else System.getenv("BINTRAY_USER")
    val bintrayApiKey = if (project.hasProperty("bintrayApiKey")) project.property("bintrayApiKey") as String else System.getenv("BINTRAY_API_KEY")

    if (bintrayUser == null || bintrayApiKey == null) {
        println("Skipping bintrayUpload task as there is no api key or user in the environment")
        return@bintray
    }

    user = bintrayUser
    key = bintrayApiKey
    override = true //not sure why it is needed
    pkg {
        repo = "dev"
        name = "GregTech: Remastered"
        userOrg = "gtr"
        setLicenses("LGPL-3.0")
        vcsUrl = "https://https://github.com/flyingperson23/GTR.git"
        version {
            name = project.version as String
            released = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT)
        }
    }
}