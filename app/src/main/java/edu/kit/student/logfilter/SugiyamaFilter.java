package edu.kit.student.logfilter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class SugiyamaFilter extends Filter<ILoggingEvent> {

  @Override
  public FilterReply decide(ILoggingEvent event) {    
    if (event.getLoggerName().startsWith("edu.kit.student.sugiyama")) {
      return FilterReply.DENY;
    } else {
      return FilterReply.ACCEPT;
    }
  }
}
