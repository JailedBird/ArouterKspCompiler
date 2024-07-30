package cn.jailedbird.arouter.ksp.service

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route

/**
 * .
 *
 * @author 985892345
 * 2023/5/28 16:58
 */
@Route(path = "/test/service")
class TestServiceImpl: ITestService1, ITestService2 {
  override fun init(context: Context) {
  }
}

@Route(path = "/test/service_generic")
class TestServiceImplGene<T> : ITestService3<T> {
  override fun init(context: Context) {
  }
}