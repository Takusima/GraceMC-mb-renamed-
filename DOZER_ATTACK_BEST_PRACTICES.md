# Лучшие практики использования механики атаки Dozer

## 1️⃣ Когда использовать атаку

### ✅ Подходит для:
- Sudden scares/jumpscares
- Environmental triggers (entering a zone)
- AI entity attacks
- Event-driven scares
- Game mechanics (survival horror)

### ❌ Не подходит для:
- Длительные визуальные эффекты (>2 сек)
- Диалоги или текстовые информационные сообщения
- Синхронизированные с игроком действия
- Непредсказуемые срабатывания

## 2️⃣ Оптимальное размещение ресурсов

```
C:\Users\Taku\Downloads\dozer\
├── dozer_paper.png          ← Скопировать в
└── dozer_attack.ogg         ← Скопировать в

d:\moding mc\на всякий\src\main\resources\assets\gracemc\
├── textures\dozer_paper.png
└── sounds\dozer_attack.ogg
```

## 3️⃣ Настройка для максимального эффекта

### A. Частота атак
```java
// ХОРОШО: Редкие, внезапные атаки (каждые 5-10 сек)
if (this.age % 200 == 0) { // Каждые 10 сек
    DozerManager.triggerDozerAttack(player);
}

// ПЛОХО: Постоянные атаки (перестают пугать)
if (this.age % 20 == 0) { // Каждую секунду
    DozerManager.triggerDozerAttack(player);
}
```

### B. Случайность
```java
// ХОРОШО: Непредсказуемые атаки
if (Math.random() < 0.15) {
    DozerManager.triggerDozerAttack(player);
}

// ПЛОХО: Всегда по расписанию (предсказуемо)
if (this.age % 100 == 0) {
    DozerManager.triggerDozerAttack(player);
}
```

### C. Условия срабатывания
```java
// ХОРОШО: Атака при специальных условиях
if (player.isPosInPlayerRange(8.0) && !player.isCreative()) {
    DozerManager.triggerDozerAttack(player);
}

// ПЛОХО: Атака без проверок
DozerManager.triggerDozerAttack(player);
```

## 4️⃣ Интеграция с AI сущностью

### Правильный способ:

```java
public class DozerEntity extends HostileEntity {
    private int attackCooldown = 0;
    
    @Override
    protected void tickMovement() {
        super.tickMovement();
        
        if (this.attackCooldown > 0) {
            this.attackCooldown--;
            return;
        }
        
        // Проверяем наличие целевого игрока
        if (this.getTarget() instanceof ServerPlayerEntity target) {
            if (this.squaredDistanceTo(target) < 64.0) { // 8 блоков
                // Атакуем!
                DozerManager.triggerDozerAttack(target);
                
                // Перезарядка: 8-12 секунд
                this.attackCooldown = 160 + this.random.nextInt(40);
            }
        }
    }
}
```

## 5️⃣ Обработка исключительных ситуаций

```java
public static void safeAttack(ServerPlayerEntity player) {
    // Проверка 1: Игрок жив?
    if (!player.isAlive()) return;
    
    // Проверка 2: Творческий режим?
    if (player.isCreative() || player.isSpectator()) return;
    
    // Проверка 3: На сервере ли?
    if (player.getServer() == null) return;
    
    // Проверка 4: Есть ли уже активная атака?
    // (Опционально, если хотите избежать спама)
    
    // Всё ОК - атакуем!
    DozerManager.triggerDozerAttack(player);
}
```

## 6️⃣ Оптимизация производительности

### ✅ Делайте:
```java
// Хорошо: Кэшируем вычисления
private double cachedDistanceSq;

@Override
public void tick() {
    this.cachedDistanceSq = this.squaredDistanceTo(target);
    
    if (this.cachedDistanceSq < 64.0) {
        DozerManager.triggerDozerAttack(target);
    }
}
```

### ❌ Не делайте:
```java
// Плохо: Много вычислений в каждый тик
if (Math.sqrt(this.squaredDistanceTo(target)) < 8.0) {
    // Вычисляем sqrt каждый тик!
}
```

## 7️⃣ Тестирование

```bash
# 1. Соберите проект
cd "d:\moding mc\на всякий"
.\gradlew build

# 2. Проверьте наличие файлов
dir src\main\resources\assets\gracemc\textures\dozer_paper.png
dir src\main\resources\assets\gracemc\sounds\dozer_attack.ogg

# 3. Запустите Minecraft
# 4. Тригернете атаку (через код или команду)
# 5. Проверьте эффекты
```

## 8️⃣ Отладка проблем

### Проблема: Текстура не показывается
**Решение:**
1. Проверьте путь: `src/main/resources/assets/gracemc/textures/dozer_paper.png`
2. Убедитесь в формате PNG
3. Пересоберите: `./gradlew clean build`

### Проблема: Звук не слышен
**Решение:**
1. Проверьте путь: `src/main/resources/assets/gracemc/sounds/dozer_attack.ogg`
2. Проверьте формат OGG Vorbis
3. Проверьте громкость в Minecraft
4. Убедитесь, что `sounds.json` содержит `dozer_attack`

### Проблема: Текст "DOZER" не виден
**Решение:**
1. Проверьте разрешение экрана
2. Убедитесь в контрасте текстуры (текст красный, может быть невиден на тёмном)
3. Проверьте масштаб экрана GUI

### Проблема: Атака не срабатывает
**Решение:**
1. Убедитесь, что игрок в выживании (не в творчестве)
2. Проверьте, что методы вызваны на сервере, не на клиенте
3. Добавьте логирование: `GraceMain.LOGGER.info("Attack triggered for " + player.getName())`

## 9️⃣ Расширение функционала

### Добавить разные типы атак

```java
public enum DozerAttackType {
    NORMAL(40, "dozer_attack", 0.6f),        // Стандартная
    FAST(30, "dozer_attack_fast", 0.8f),    // Быстрая
    INTENSE(50, "dozer_attack_intense", 0.4f); // Интенсивная
    
    public final int duration;
    public final String sound;
    public final float pitch;
    
    DozerAttackType(int duration, String sound, float pitch) {
        this.duration = duration;
        this.sound = sound;
        this.pitch = pitch;
    }
}
```

### Добавить параметры атаке

```java
public static void triggerDozerAttack(ServerPlayerEntity player, DozerAttackType type) {
    // Настроить параметры в зависимости от типа
    // ...
}
```

## 🔟 Контрольный список перед релизом

- [ ] ✅ Текстура добавлена (`dozer_paper.png`)
- [ ] ✅ Звук добавлен (`dozer_attack.ogg`)
- [ ] ✅ Проект компилируется без ошибок
- [ ] ✅ Визуальные эффекты видны
- [ ] ✅ Звук слышен
- [ ] ✅ Текст "DOZER" отрисовывается
- [ ] ✅ Атака срабатывает при вызове
- [ ] ✅ Нет замедления времени
- [ ] ✅ Камера свободна
- [ ] ✅ Атака длится ~2 секунды

---

**Последнее обновление:** 29 апреля 2026
