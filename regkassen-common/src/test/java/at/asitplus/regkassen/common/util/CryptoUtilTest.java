/*
 * Copyright (C) 2015
 * A-SIT Plus GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.asitplus.regkassen.common.util;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigInteger;
import java.security.SecureRandom;

public class CryptoUtilTest {

  @Test(dataProvider = "byteProvider")
  public void testByteArrayToLong(byte[] bytes, long expected) {

    Assert.assertEquals(CryptoUtil.getLong(bytes), expected);
  }

  @DataProvider
  public Object[][] byteProvider() {

    final  int NUM_VALUES = 200;
    Object[][] res = new Object[NUM_VALUES][2];
    SecureRandom secureRandom = new SecureRandom();

    for (int i = 0; i < NUM_VALUES; ++i) {

      byte[] arr;
      long number;
      do {
        number = Integer.MAX_VALUE;
        number += secureRandom.nextInt();
        if (i % 2 == 0)
          number += secureRandom.nextInt();
        if (i % 3 == 0)
          number += secureRandom.nextInt();
        if (i % 5 == 0)
          number += secureRandom.nextInt();
        if (i % 7 == 0)
          number += secureRandom.nextInt();
        if (i % 11 == 0)
          number += secureRandom.nextInt();
        if (i % 13 == 0)
          number += secureRandom.nextInt();
       
        if (i % 2 == 0) {
          number = -number;
        }

        arr = BigInteger.valueOf(number).toByteArray();
      } while (arr.length != 5);

      res[i][0] = arr;
      res[i][1] = number;
    }
    return res;

  }
}
