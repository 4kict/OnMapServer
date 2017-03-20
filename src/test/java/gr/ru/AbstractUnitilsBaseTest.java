package gr.ru;


import org.junit.Before;
import org.unitils.UnitilsJUnit4;
import org.unitils.database.DatabaseUnitils;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.util.TransactionMode;
import org.unitils.spring.annotation.SpringApplicationContext;

/**
 * Created by SBT-Babich-VR on 18.02.2017.
 */

@SpringApplicationContext({"/unitTestContext.xml"})
@Transactional(TransactionMode.DISABLED)
public class AbstractUnitilsBaseTest extends UnitilsJUnit4 {

    @Before
    public void init() {
        DatabaseUnitils.disableConstraints();
    }

}
