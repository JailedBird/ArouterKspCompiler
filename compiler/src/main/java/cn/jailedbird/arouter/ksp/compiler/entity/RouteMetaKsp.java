package cn.jailedbird.arouter.ksp.compiler.entity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.enums.RouteType;
import com.alibaba.android.arouter.facade.model.RouteMeta;
import com.google.devtools.ksp.symbol.KSClassDeclaration;

import java.util.Map;

import javax.lang.model.element.Element;

public class RouteMetaKsp extends RouteMeta {
    private KSClassDeclaration kspRawTypeInner;      // Raw type of ksp : KSClassDeclaration

    public RouteMetaKsp(Route route, Element rawType, RouteType type, Map<String, Integer> paramsType) {
        super(type, rawType, null, route.name(), route.path(), route.group(), paramsType, route.priority(), route.extras());
    }

    /**
     * Type
     *
     * @param route      route
     * @param kspRawType kspRawType
     * @param type       type
     * @param paramsType paramsType
     */
    public static RouteMeta build(Route route, KSClassDeclaration kspRawType, RouteType type, Map<String, Integer> paramsType) {
        RouteMetaKsp meta = new RouteMetaKsp(route, null, type, paramsType);
        meta.kspRawTypeInner = kspRawType;
        return meta;
    }

    public KSClassDeclaration getKspRawTypeInner() {
        return kspRawTypeInner;
    }
}
