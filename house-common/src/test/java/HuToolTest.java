import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.swing.ClipboardUtil;
import org.junit.Test;

import java.util.ArrayList;

public class HuToolTest {
    @Test
    public void test(){
        ClipboardUtil.setStr("hello world");
        ArrayList<Object> objects = CollUtil.newArrayList();
    }
}
