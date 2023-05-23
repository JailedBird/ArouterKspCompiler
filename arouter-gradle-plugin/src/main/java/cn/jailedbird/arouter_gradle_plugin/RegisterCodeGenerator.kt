package cn.jailedbird.arouter_gradle_plugin

import cn.jailedbird.arouter_gradle_plugin.utils.ScanSetting
import org.objectweb.asm.*
import java.io.InputStream

/**
 * generate register code into LogisticsCenter.class
 */
class RegisterCodeGenerator(private val extensions: List<ScanSetting>) {

    // refer hack class when object init
    fun referHackWhenInit(inputStream: InputStream): ByteArray {
        val cr = ClassReader(inputStream)
        val cw = ClassWriter(cr, 0)
        val cv = MyClassVisitor(Opcodes.ASM5, cw)
        cr.accept(cv, ClassReader.EXPAND_FRAMES)
        return cw.toByteArray()
    }

    private inner class MyClassVisitor(api: Int, cv: ClassVisitor) : ClassVisitor(api, cv) {

        override fun visit(
            version: Int,
            access: Int,
            name: String,
            signature: String?,
            superName: String?,
            interfaces: Array<String>?
        ) {
            super.visit(version, access, name, signature, superName, interfaces)
        }

        override fun visitMethod(
            access: Int,
            name: String,
            desc: String,
            signature: String?,
            exceptions: Array<String>?
        ): MethodVisitor {
            var mv = super.visitMethod(access, name, desc, signature, exceptions)
            // generate code into this method
            if (name == ScanSetting.GENERATE_TO_METHOD_NAME) {
                mv = RouteMethodVisitor(Opcodes.ASM5, mv)
            }
            return mv
        }
    }

    private inner class RouteMethodVisitor(api: Int, mv: MethodVisitor) : MethodVisitor(api, mv) {

        override fun visitInsn(opcode: Int) {
            // generate code before return
            if (opcode in Opcodes.IRETURN..Opcodes.RETURN) {
                extensions.forEach { scanSetting ->
                    scanSetting.classList.forEach { name ->
                        val className = name.replace("/", ".")
                        mv.visitLdcInsn(className)// 类名
                        // generate invoke register method into LogisticsCenter.loadRouterMap()
                        mv.visitMethodInsn(
                            Opcodes.INVOKESTATIC,
                            ScanSetting.GENERATE_TO_CLASS_NAME,
                            ScanSetting.REGISTER_METHOD_NAME,
                            "(Ljava/lang/String;)V",
                            false
                        )
                    }
                }
            }
            super.visitInsn(opcode)
        }

        override fun visitMaxs(maxStack: Int, maxLocals: Int) {
            super.visitMaxs(maxStack + 4, maxLocals)
        }
    }
}
