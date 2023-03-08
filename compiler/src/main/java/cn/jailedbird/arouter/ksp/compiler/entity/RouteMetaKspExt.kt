package cn.jailedbird.arouter.ksp.compiler.entity

import com.alibaba.android.arouter.facade.model.RouteMeta
import com.google.devtools.ksp.symbol.KSClassDeclaration

internal val RouteMeta.toKspRawType: KSClassDeclaration
    get() = (this as RouteMetaKsp).kspRawType
