package com.github.mvp4g.nalu.client.component;

public interface IsController<W> {

  W asElement();

  void onAttach();

  void onDetach();

  String mayStop();

  void removeHandlers();

  void start();

  void stop();

}
