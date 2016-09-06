/*
 * Copyright (C) 2015, 2016
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

package at.asitplus.regkassen.common;

/**
 * enum representing the turnovercounter-type in a receipt, could be
 * normal (meaning that encrypted turnover counter is stored in receipt)
 * TRA (indicating training receipt)
 * STO (indicating "Storno" receipt)
 */
public enum TurnoverCounterType {
    NORMAL("", ""),
    TRA("VFJB", "TRA"),
    STO("U1RP", "STO");

  private final String encodedValue, decodedValue;

  private TurnoverCounterType(String encodedValue, String decodedValue) {
    this.encodedValue = encodedValue;
    this.decodedValue = decodedValue;
  }

  public static TurnoverCounterType toTurnoverCunterType(String encodedValue) {
    for (TurnoverCounterType type : TurnoverCounterType.values())
      if (type.encodedValue.equals(encodedValue))
        return type;
    return NORMAL;
  }

  public String getEncodedValue() {
    return encodedValue;
  }

  public String getDecodedValue() {
    return decodedValue;
  }
}
