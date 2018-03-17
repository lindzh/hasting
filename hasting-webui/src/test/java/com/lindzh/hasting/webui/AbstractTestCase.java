package com.lindzh.hasting.webui;

/**
 * Created by lin on 2016/12/17.
 */

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:mybatis-config.xml","classpath:spring-admin.xml"})
public abstract class AbstractTestCase {

}
