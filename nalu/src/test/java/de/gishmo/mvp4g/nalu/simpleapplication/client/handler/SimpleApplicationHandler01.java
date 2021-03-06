package de.gishmo.mvp4g.nalu.simpleapplication.client.handler;

import com.github.mvp4g.nalu.client.handler.AbstractHandler;
import com.github.mvp4g.nalu.client.handler.annotation.Handler;
import de.gishmo.mvp4g.nalu.simpleapplication.client.NaluSimpleApplicationContext;
import de.gishmo.mvp4g.nalu.simpleapplication.client.event.StatusChangeEvent;

@Handler
public class SimpleApplicationHandler01
    extends AbstractHandler<NaluSimpleApplicationContext> {

  public SimpleApplicationHandler01() {
  }

  @Override
  public void bind() {
    this.eventBus.addHandler(StatusChangeEvent.TYPE,
                             e -> {
                               // Stupid idea! It should only show, that the event was catched by the handler!
                               System.out.print("new Status:" + e.getStatus());
                             });
  }
}
