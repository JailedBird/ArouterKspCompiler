package cn.jailedbird.arouter.ksp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import cn.jailedbird.arouter.ksp.service.TestServiceImpl
import cn.jailedbird.arouter.ksp.service.TestServiceImplGene
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

    @Autowired(name = "/test/service1")
    lateinit var iProvider1: TestServiceImpl
//
//    @Autowired
//    var iProvider2: TestServiceImpl? = null
//
//    @Autowired
//    lateinit var iProvider3: TestServiceImplGene<Int>
//
//    @Autowired
//    var iProvider4: TestServiceImplGene<Int>? = null
//
//    @Autowired(name = "iProvider5")
//    lateinit var iProvider5: TestServiceImpl
//
//    @Autowired(name = "iProvider6")
//    var iProvider6: TestServiceImpl? = null
//
//    @Autowired(name = "iProvider7")
//    lateinit var iProvider7: TestServiceImplGene<Int>
//
//    @Autowired(name = "iProvider8")
//    var iProvider8: TestServiceImplGene<Int>? = null

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


    @Autowired(name = "hashSetttt")
    var hasSet = HashSet<LinkedHashMap<String, ArrayList<Int>>>()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
        setContentView(R.layout.second_main)
        Log.d("TAG", arrayList.toString())
        Log.d("TAG", lateArrayList.toString())
        Log.d("TAG", list.toString())
        Log.d("TAG", lateList.toString())
        Log.d("TAG", str)
        Log.d("TAG", lateStr)
        iProvider1.debug()

    }
}