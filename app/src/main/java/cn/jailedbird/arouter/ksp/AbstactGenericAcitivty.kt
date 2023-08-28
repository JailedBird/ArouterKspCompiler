package cn.jailedbird.arouter.ksp

import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route

interface TestInterface1
interface TestInterface2

abstract class AbstactGenericAcitivty1<T : TestInterface1> : AppCompatActivity() {
    @Autowired
    var list = mutableListOf<String>()
}

abstract class AbstactGenericAcitivty2<T : TestInterface1, G:TestInterface2> : AppCompatActivity() {
    @Autowired
    var list = mutableListOf<String>()
}

@Route(path = "/app/TestAbstactGenericAcitivty1")
class TestAbstactGenericAcitivty1 : AbstactGenericAcitivty1<TestInterface1>(){
    @Autowired
    var t = mutableListOf<String>()
}

@Route(path = "/app/TestAbstactGenericAcitivty2")
class TestAbstactGenericAcitivty2 : AbstactGenericAcitivty2<TestInterface1, TestInterface2>(){
    @Autowired
    var t = mutableListOf<String>()
}