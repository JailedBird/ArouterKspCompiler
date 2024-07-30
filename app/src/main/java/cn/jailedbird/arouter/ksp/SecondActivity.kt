package cn.jailedbird.arouter.ksp

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.jailedbird.arouter.ksp.service.ITestService1
import cn.jailedbird.arouter.ksp.service.ITestService2
import cn.jailedbird.arouter.ksp.service.ITestService3
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

@Suppress("unused")
@Route(path = "/app/SecondActivity")
class SecondActivity : AppCompatActivity() {

    @Autowired
    var str: String = ""

    @Autowired
    var strNull: String? = null

    @Autowired
    lateinit var lateStr: String

    @Autowired
    lateinit var iProvider1: ITestService1

    @Autowired
    var iProvider2: ITestService2? = null

    @Autowired
    lateinit var iProvider3: ITestService3<Int>

    @Autowired
    var iProvider4: ITestService3<Int>? = null

    @Autowired(name = "/test/service")
    lateinit var iProvider5: ITestService1

    @Autowired(name = "/test/service")
    var iProvider6: ITestService2? = null

    @Autowired(name = "/test/service_generic")
    lateinit var iProvider7: ITestService3<Int>

    @Autowired(name = "/test/service_generic")
    var iProvider8: ITestService3<Int>? = null

    @Autowired
    var list = mutableListOf<String>()

    @Autowired
    lateinit var lateList: MutableList<String>

    @Autowired(required = true)
    var arrayList = arrayListOf<String>()

    @Autowired(required = true)
    lateinit var lateArrayList: ArrayList<String>

    @Autowired
    var array = LinkedHashMap<String, ArrayList<Int>>()

    @Autowired
    lateinit var lateArray: LinkedHashMap<String, ArrayList<Int>>


    @Autowired(name = "hashSet")
    var hashSet = HashSet<LinkedHashMap<String, ArrayList<Int>>>()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
        setContentView(R.layout.second_main)
        findViewById<TextView>(android.R.id.text1).text = listOf(
            arrayList,
            lateArrayList,
            list,
            lateList,
            str,
            lateStr,
            hashSet
        ).joinToString("\n")
        Log.d("TAG", arrayList.toString())
        Log.d("TAG", lateArrayList.toString())
        Log.d("TAG", list.toString())
        Log.d("TAG", lateList.toString())
        Log.d("TAG", str)
        Log.d("TAG", lateStr)
        Log.d("TAG", hashSet.toString())

    }
}