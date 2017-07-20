package com.arsoft.agendate.json.typeconverter;

import com.bluelinelabs.logansquare.typeconverters.StringBasedTypeConverter;

import java.math.BigDecimal;

/**
 * Created by dbonnin@visionbanco.com on 8/17/2016.
 */

public class StringToBigDecimal extends StringBasedTypeConverter<BigDecimal> {

  @Override
  public BigDecimal getFromString(String string) {
    return new BigDecimal(string);
  }

  @Override
  public String convertToString(BigDecimal object) {
    return object.toString();
  }


}
