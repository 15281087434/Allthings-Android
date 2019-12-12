package songqiu.allthings.bean;

import java.io.Serializable;
import java.util.List;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/15
 *
 *类描述：
 *
 ********/
public class AllClassificationBean {
//    {
//        "category": "1",
//            "name": "奇闻趣事",
//            "row1": [{
//        "name": "奇闻"
//    }, {
//        "name": "怪谈"
//    }, {
//        "name": "幽默"
//    }, {
//        "name": "神话"
//    }]
//    }

    public int category;
    public String name;
    public List<ClassificationSubitemBean> row1;

}
