package cn.jailedbird.arouter.ksp.service

import android.content.Context
import android.util.Log
import com.alibaba.android.arouter.facade.annotation.Route

/**
 * .
 *
 * @author 985892345
 * 2023/5/28 16:58
 */
@Route(path = "/test/service1")
class TestServiceImpl: ITestService1, ITestService2 {
  override fun debug() {
    Log.d("TAG", "TestServiceImpl /test/service1")
  }

  override fun init(context: Context) {
  }
}

@Route(path = "/test/service2")
class TestServiceImplGene<T> : ITestService1, ITestService2 {
  override fun debug() {
    Log.d("TAG", "TestServiceImplGene /test/service2")
  }

  override fun init(context: Context) {
  }
}