# Реализация механики атаки Dozer для GraceMC

## ✅ Статус: УСПЕШНО РЕАЛИЗОВАНО И СКОМПИЛИРОВАНО

Проект успешно скомпилирован (BUILD SUCCESSFUL). Все компоненты установлены и готовы к использованию.

---

## 📋 Что было сделано

### 1. **Новое состояние ATTACK** 
   - Файл: [src/main/java/com/GraceMC/logic/DozerState.java](src/main/java/com/GraceMC/logic/DozerState.java)
   - Добавлено состояние `ATTACK` для мгновенной атаки

### 2. **DozerAttackHandler** (новый класс)
   - Файл: [src/main/java/com/GraceMC/logic/DozerAttackHandler.java](src/main/java/com/GraceMC/logic/DozerAttackHandler.java)
   - Публичный метод: `triggerDozer(ServerPlayerEntity player)`
   - Длительность атаки: 40 тиков (~2 секунды)

### 3. **Обновлен HudRenderer** 
   - Файл: [src/main/java/com/GraceMC/client/DozerHudRenderer.java](src/main/java/com/GraceMC/client/DozerHudRenderer.java)
   - Новая функция: `drawDozerAttackText()` для отрисовки текста
   - Поддержка ATTACK состояния с полноэкранной текстурой
   - Большой красный текст "DOZER" (масштаб 5x) в центре нижней части экрана
   - Синхронизированный звук при появлении

### 4. **Обновлен DozerManager** 
   - Файл: [src/main/java/com/GraceMC/logic/DozerManager.java](src/main/java/com/GraceMC/logic/DozerManager.java)
   - Новый метод: `triggerDozerAttack(ServerPlayerEntity player)`
   - Обработка ATTACK состояния на сервере
   - **Критично: БЕЗ эффектов замедления** (NO applyFreeze)

### 5. **Обновлены звуки** 
   - Файл: [src/main/resources/assets/gracemc/sounds.json](src/main/resources/assets/gracemc/sounds.json)
   - Новый звук: `dozer_attack`
   - Параметры: громкость 1.0f, pitch 0.6f (страшный низкий тон)

### 6. **Текстура (заглушка)**
   - Файл: [src/main/resources/assets/gracemc/textures/dozer_paper.png](src/main/resources/assets/gracemc/textures/dozer_paper.png)
   - Текущая текстура - временная (скопирована из `angry_dozer.png` для тестирования)

---

## 🎮 Как использовать

### Вызов атаки из кода

```java
import com.GraceMC.logic.DozerManager;
import net.minecraft.server.network.ServerPlayerEntity;

// Вариант 1: Через DozerManager
ServerPlayerEntity player = /* получить игрока */;
DozerManager.triggerDozerAttack(player);

// Вариант 2: Через DozerAttackHandler
import com.GraceMC.logic.DozerAttackHandler;
DozerAttackHandler.triggerDozer(player);
```

### Вызов из AI сущности

```java
// В классе вашей сущности
@Override
protected void tickAi() {
    if (shouldAttackPlayer()) {
        DozerManager.triggerDozerAttack(this.targetPlayer);
    }
}
```

---

## 📦 Требуемые файлы ресурсов

### 1. Текстура `dozer_paper.png`
**Путь:** `src/main/resources/assets/gracemc/textures/dozer_paper.png`

Ваша текстура из архива должна быть размещена в этом месте:
- Скопируйте файл из: `C:\Users\Taku\Downloads\dozer\dozer_paper.png`
- В: `d:\moding mc\на всякий\src\main\resources\assets\gracemc\textures\dozer_paper.png`

**Характеристики текстуры:**
- Формат: PNG
- Рекомендуемый размер: 512x512 или больше
- Будет растянута на весь экран (fullscreen overlay)
- Оптимально: белая или светлая текстура на тёмном фоне

### 2. Звук `dozer_attack.ogg`
**Путь:** `src/main/resources/assets/gracemc/sounds/dozer_attack.ogg`

Разместите файл звука атаки:
- Из: `C:\Users\Taku\Downloads\dozer\dozer_attack.ogg`
- В: `d:\moding mc\на всякий\src\main\resources\assets\gracemc\sounds\dozer_attack.ogg`

**Характеристики звука:**
- Формат: OGG (Vorbis)
- Длительность: рекомендуется 0.5-1 секунда
- Громкость: будет проигрываться на 1.0f (максимум)
- Pitch: 0.6f (низкий, страшный тон)

---

## ⚙️ Технические характеристики

| Параметр | Значение |
|----------|----------|
| Длительность атаки | 40 тиков (2 секунды при 20 TPS) |
| Появление | Мгновенное (1 тик) |
| Исчезновение | Мгновенное (1 тик) |
| Масштаб текста "DOZER" | 5.0x |
| Цвет текста | Красный (#FF0000) |
| Громкость звука | 1.0f (максимум) |
| Pitch звука | 0.6f (низкий тон) |
| Эффекты на игрока | **НЕТУ** - полная свобода движения |
| Замедление времени | **НЕТ** - стандартная скорость игры |

### Поведение
✅ **Нет GUI/Screen классов** - используется только HudRenderCallback
✅ **Полная свобода камеры** - игрок может вращать головой
✅ **Никакого замедления** - обычная скорость игры
✅ **Синхронизация звука** - звук проигрывается при появлении
✅ **Мгновенность** - нет анимаций вход/выхода

---

## 🔧 Структура проекта

```
src/main/java/com/GraceMC/
├── logic/
│   ├── DozerState.java (обновлён: добавлено ATTACK)
│   ├── DozerAttackHandler.java (новый класс)
│   └── DozerManager.java (обновлён: triggerDozerAttack)
└── client/
    └── DozerHudRenderer.java (обновлён: поддержка ATTACK)

src/main/resources/assets/gracemc/
├── sounds.json (обновлён: добавлен dozer_attack)
├── sounds/
│   └── dozer_attack.ogg (ТРЕБУЕТСЯ добавить)
└── textures/
    └── dozer_paper.png (ТРЕБУЕТСЯ добавить)
```

---

## 📝 Дальнейшие шаги

### Немедленно

1. **Добавьте текстуру:**
   ```
   Скопируйте: C:\Users\Taku\Downloads\dozer\dozer_paper.png
   В: d:\moding mc\на всякий\src\main\resources\assets\gracemc\textures\dozer_paper.png
   ```

2. **Добавьте звук:**
   ```
   Скопируйте: C:\Users\Taku\Downloads\dozer\dozer_attack.ogg
   В: d:\moding mc\на всякий\src\main\resources\assets\gracemc\sounds\dozer_attack.ogg
   ```

3. **Пересоберите проект:**
   ```bash
   cd "d:\moding mc\на всякий"
   ./gradlew build
   ```

### Интеграция в AI

Добавьте вызов в ваш класс сущности Dozer:

```java
// В методе атаки
if (canAttackPlayer && this.age % 20 == 0) {
    DozerManager.triggerDozerAttack(targetPlayer);
}
```

---

## 🧪 Тестирование

Проект **успешно компилируется**. Для тестирования:

1. Соберите проект: `./gradlew build`
2. Запустите клиент Minecraft
3. Вызовите атаку через код (см. выше)
4. Вы должны увидеть:
   - Полноэкранную текстуру
   - Большой красный текст "DOZER"
   - Услышать звук атаки
   - На протяжении ~2 секунд

---

## 🔴 Важные требования

✅ **Соблюдены:**
- ✓ Без GUI (Screen/HandledScreen)
- ✓ Полная свобода камеры
- ✓ Никакого замедления
- ✓ HudRenderCallback для отрисовки
- ✓ Полноэкранная текстура
- ✓ Большой красный текст "DOZER"
- ✓ Синхронизация звука
- ✓ Мгновенное появление/исчезновение
- ✓ Публичный метод triggerDozer()

---

## 📞 Версия

- **Minecraft:** 1.21.1
- **Fabric API:** 0.102.0+1.21.1
- **Java:** 21
- **Gradle:** 9.4.1

---

## 📄 Дополнительные файлы

- [DOZER_ATTACK_SETUP.md](DOZER_ATTACK_SETUP.md) - Подробные инструкции по настройке
- [src/main/resources/assets/gracemc/sounds.json](src/main/resources/assets/gracemc/sounds.json) - Конфигурация звуков

---

**Последнее обновление:** 29 апреля 2026
**Статус:** ✅ ГОТОВО К ИСПОЛЬЗОВАНИЮ
