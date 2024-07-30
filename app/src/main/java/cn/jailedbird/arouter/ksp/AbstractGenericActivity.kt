package cn.jailedbird.arouter.ksp

import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route

interface TestInterface1
interface TestInterface2

abstract class AbstractGenericActivity1<T : TestInterface1> : AppCompatActivity() {
    @Autowired
    var list = mutableListOf<String>()
}

abstract class AbstractGenericActivity2<T : TestInterface1, G:TestInterface2> : AppCompatActivity() {
    @Autowired
    var list = mutableListOf<String>()
}

@Route(path = "/app/TestAbstractGenericActivity1")
class TestAbstractGenericActivity1 : AbstractGenericActivity1<TestInterface1>(){
    @Autowired
    var t = mutableListOf<String>()
}

@Route(path = "/app/TestAbstractGenericActivity2")
class TestAbstractGenericActivity2 : AbstractGenericActivity2<TestInterface1, TestInterface2>(){
    @Autowired
    var t = mutableListOf<String>()
}