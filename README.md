# ChatLogger — Автологгер чата для Minecraft 1.16.5 (Fabric)

Автоматически сохраняет весь чат в файлы по серверам. Полный лог с таймстампами и разделителями сессий.

## Установка

1. Установи [Fabric Loader](https://fabricmc.net/) для Minecraft 1.16.5
2. Положи `chatlogger-fabric-1.16.5-1.0.0.jar` и [Fabric API](https://modrinth.com/mod/fabric-api) в `mods/`
3. Запусти Minecraft

## Как работает

Мод работает полностью в фоне, без горячих клавиш и интерфейса.

- Каждый сервер — отдельный файл в `.minecraft/chat/`
- Имя файла = название сервера (напр. `Hypixel.txt`)
- При входе на сервер пишется заголовок сессии (время + IP)
- При выходе — завершающая строка
- Весь чат логируется с временными метками `[HH:mm:ss]`

## Пример лога

```
[2026-08-26 15:30:00] === Сервер: Hypixel | IP: mc.hypixel.net ===
[15:30:12] Steve joined the game
[15:30:15] <Alex> Привет!
[15:30:18] <Steve> Привет, как дела?
[15:30:25] <Alex> Нормально, играем?
[2026-08-26 16:00:00] === Конец сессии ===
```

## Где файлы

```
.minecraft/chat/
├── Hypixel.txt
├── Singleplayer.txt
└── Мой сервер.txt
```

## Конфигурация

Мод не требует настройки. Работает сразу после установки.

## Сборка из исходников

```bash
git clone https://github.com/YOUR_NAME/chatlogger.git
cd chatlogger
./gradlew build
# jar будет в build/libs/
```

## Требования

- Minecraft 1.16.5
- Fabric Loader 0.11+
- Fabric API 0.42.0+1.16
- Java 8+

## Лицензия

MIT
