package com.synet.generate;

import com.csvreader.CsvReader;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;

public class JavaCSVTest {

    @Test
    public void read() {

        String filePath = "database/s_user.csv";

        try {
            // 创建CSV读对象
            CsvReader csvReader = new CsvReader(filePath, ',', Charset.forName("GBK"));


            // 读表头
            //csvReader.readHeaders();
            while (csvReader.readRecord()) {
                // 读一整行
                System.out.println(csvReader.getRawRecord());
                // 读这行的某一列
                System.out.println(csvReader.get("Link"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
