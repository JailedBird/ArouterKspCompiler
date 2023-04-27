package cn.jailedbird.arouter_gradle_plugin

import org.gradle.api.Project

/**
 * Format log
 *
 * @author zhilong <a href="mailto:zhilong.lzl@alibaba-inc.com">Contact me.</a>
 * @version 1.0
 * @since 2017/12/18 下午2:43
 */
object Logger {
    private var logger: org.gradle.api.logging.Logger? = null

    fun make(project: Project) {
        logger = project.logger
    }

    fun i(info: String?) {
        if (!info.isNullOrBlank() && logger != null) {
            logger!!.info("ARouter::Register >>> $info")
        }
    }

    fun e(error: String?) {
        if (!error.isNullOrBlank() && logger != null) {
            logger!!.error("ARouter::Register >>> $error")
        }
    }

    fun w(warning: String?) {
        if (!warning.isNullOrBlank() && logger != null) {
            logger!!.warn("ARouter::Register >>> $warning")
        }
    }
}

