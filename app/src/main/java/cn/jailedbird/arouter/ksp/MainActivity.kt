package cn.jailedbird.arouter.ksp

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.jailedbird.arouter.ksp.service.ITestService1
import cn.jailedbird.arouter.ksp.service.ITestService2
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.launcher.ARouter

class MainActivity : AppCompatActivity() {

    @Autowired
    var list = mutableListOf<String>()

    @Autowired
    var arrayList = arrayListOf<String>()

    @Autowired
    var array = LinkedHashMap<String, ArrayList<Int>>()

    @Autowired
    var hasSet = HashSet<LinkedHashMap<String, ArrayList<Int>>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.jump).setOnClickListener {
            ARouter.getInstance().build("/app/SecondActivity")
                .withObject("arrayList", arrayListOf("1", "2", "3"))
                .withObject("lateArrayList", arrayListOf("1", "2", "3"))
                .withObject("list", mutableListOf("1", "2", "3", "4"))
                .withObject("lateList", mutableListOf("1", "2", "3", "4"))
                .withObject("hashSet", hashSetOf(hashMapOf(
                    "a" to arrayListOf(1, 2, 3, 4),
                    "b" to arrayListOf(5, 6, 7, 8),
                )))
                .withString("str", "Hello world, String")
                .withString("lateStr", "Hello world. Late string")
                .navigation(this)
        }
        
        val testService1 = ARouter.getInstance()
            .navigation(ITestService1::class.java)
        val testService2 = ARouter.getInstance()
            .navigation(ITestService2::class.java)
        if (testService1 != null && testService2 != null && testService1 === testService2) {
            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
        }
    }
}