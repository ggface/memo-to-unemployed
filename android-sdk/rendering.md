# Рендеринг

### Что происходит при invalidate?
invalidate помечает View как dirty, 
далее Choreographer по VSync вызывает doFrame, 
на UI thread происходит measure/layout/draw, 
формируется DisplayList (RenderNode), 
который передаётся RenderThread для исполнения, 
далее GPU рисует кадр 
и SurfaceFlinger выводит его на экран.

**UI Thread (Main Thread)**
- View hierarchy
- Compose runtime
- обработка input событий

[**Choreographer**](https://developer.android.com/reference/android/view/Choreographer)
Координирует тайминги animations, input and drawing.
синхронизирует UI с VSync. 
Он получает сигнал от дисплея и вызывает doFrame() на UI thread (Looper)

👉 Он гарантирует, что кадры рисуются в такт экрану.

**VSync** — это сигнал от дисплея, который приходит 
с частотой обновления (например, 60Hz). 
Он говорит системе, когда нужно рисовать следующий кадр, 
чтобы избежать пропусков кадров и синхронизировать рендеринг.

**DisplayList** это записанный набор команд рисования.

Он создаётся на UI Thread во время draw() и затем передаётся в Render Thread, который его исполняет.

**Render Thread**
- получает DisplayList от UI Thread
- исполняет команды рисования
- передаёт их GPU

**Jank** — это пропуск кадров (lag). Возникает, когда:

UI Thread или Render Thread не укладываются в бюджет кадра (~16ms)

