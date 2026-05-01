/**
 * ПРИМЕРЫ ИСПОЛЬЗОВАНИЯ МЕХАНИКИ АТАКИ DOZER
 * Minecraft 1.21.1 + Fabric API
 */

// ============================================================================
// ПРИМЕР 1: Простой вызов атаки из обработчика события
// ============================================================================

import com.GraceMC.logic.DozerManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

public class DozerAttackExample {
    public static void setupAttackTrigger() {
        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            // Пример: атакуем первого игрока каждые 10 секунд
            if (server.getTicks() % 200 == 0) {
                var players = server.getPlayerManager().getPlayerList();
                if (!players.isEmpty()) {
                    DozerManager.triggerDozerAttack(players.get(0));
                }
            }
        });
    }
}

// ============================================================================
// ПРИМЕР 2: Вызов атаки из AI сущности
// ============================================================================

import com.GraceMC.logic.DozerManager;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class DozerAttackGoal extends Goal {
    private final HostileEntity dozer;
    private ServerPlayerEntity targetPlayer;
    private int attackCooldown;
    
    public DozerAttackGoal(HostileEntity dozer) {
        this.dozer = dozer;
        this.attackCooldown = 0;
    }
    
    @Override
    public boolean canStart() {
        if (this.dozer.getTarget() instanceof ServerPlayerEntity) {
            this.targetPlayer = (ServerPlayerEntity) this.dozer.getTarget();
            return this.dozer.squaredDistanceTo(this.targetPlayer) < 64.0; // Атакуем в радиусе 8 блоков
        }
        return false;
    }
    
    @Override
    public void tick() {
        if (this.attackCooldown > 0) {
            this.attackCooldown--;
            return;
        }
        
        // Запускаем атаку
        DozerManager.triggerDozerAttack(this.targetPlayer);
        
        // Даём 10 секунд перезарядки
        this.attackCooldown = 200;
    }
}

// ============================================================================
// ПРИМЕР 3: Атака при определённом условии
// ============================================================================

import com.GraceMC.logic.DozerAttackHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class DozerTriggerCondition {
    /**
     * Запустить атаку, если игрок входит в определённую зону
     */
    public static void triggerIfInZone(ServerPlayerEntity player, double zoneX, double zoneY, double zoneZ, double radius) {
        double distance = Math.sqrt(
            Math.pow(player.getX() - zoneX, 2) +
            Math.pow(player.getY() - zoneY, 2) +
            Math.pow(player.getZ() - zoneZ, 2)
        );
        
        if (distance < radius) {
            // Используем DozerAttackHandler вместо DozerManager
            DozerAttackHandler.triggerDozer(player);
        }
    }
    
    /**
     * Запустить атаку с случайной вероятностью
     */
    public static void triggerRandomly(ServerPlayerEntity player, double probability) {
        if (Math.random() < probability) {
            DozerManager.triggerDozerAttack(player);
        }
    }
}

// ============================================================================
// ПРИМЕР 4: Последовательные атаки
// ============================================================================

import com.GraceMC.logic.DozerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.HashMap;
import java.util.Map;

public class DozerSequentialAttacks {
    private static final Map<String, Integer> ATTACK_COUNTERS = new HashMap<>();
    private static final int ATTACKS_PER_SEQUENCE = 3;
    private static final int DELAY_BETWEEN_ATTACKS = 100; // 5 секунд
    
    public static void triggerSequentialAttacks(ServerPlayerEntity player) {
        String uuid = player.getUuidAsString();
        ATTACK_COUNTERS.put(uuid, 0);
        scheduleNextAttack(player, uuid);
    }
    
    private static void scheduleNextAttack(ServerPlayerEntity player, String uuid) {
        int count = ATTACK_COUNTERS.getOrDefault(uuid, 0);
        
        if (count >= ATTACKS_PER_SEQUENCE) {
            ATTACK_COUNTERS.remove(uuid);
            return;
        }
        
        // Запускаем атаку
        DozerManager.triggerDozerAttack(player);
        ATTACK_COUNTERS.put(uuid, count + 1);
        
        // Расписываем следующую атаку
        player.getServer().execute(() -> {
            try {
                Thread.sleep(DELAY_BETWEEN_ATTACKS * 50); // Конвертируем тики в миллисекунды
                scheduleNextAttack(player, uuid);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
}

// ============================================================================
// ПРИМЕР 5: Интеграция с блоком/сущностью события
// ============================================================================

import com.GraceMC.logic.DozerManager;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class DozerBlockBreakTrigger {
    public static void init() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (!(player instanceof ServerPlayerEntity)) return;
            
            // Атакуем игрока с вероятностью 10% при разрушении блока
            if (Math.random() < 0.1) {
                DozerManager.triggerDozerAttack((ServerPlayerEntity) player);
            }
        });
    }
}

// ============================================================================
// ПРИМЕР 6: Настройка длительности атаки
// ============================================================================

/*
Если вы хотите изменить длительность атаки, отредактируйте:

Файл: src/main/java/com/GraceMC/logic/DozerAttackHandler.java

    private static final int ATTACK_DURATION_TICKS = 40; // 1.5-2 seconds (40 ticks at 20 TPS)

Измените значение:
- 20 тиков = 1 секунда
- 40 тиков = 2 секунды (текущее значение)
- 60 тиков = 3 секунды
- 100 тиков = 5 секунд
*/

// ============================================================================
// ПРИМЕР 7: Настройка звука и текста
// ============================================================================

/*
Если вы хотите изменить звук, отредактируйте:

Файл: src/main/java/com/GraceMC/client/DozerHudRenderer.java

    client.player.playSound(DOZER_ATTACK_SOUND, 1.0F, 0.6F);
                                                  ^    ^
                                              громкость pitch

- Громкость: 0.0f - 1.0f (текущее 1.0f = максимум)
- Pitch: 0.5f-2.0f (0.5f = низко, 1.0f = нормально, 2.0f = высоко)

Если вы хотите изменить текст, отредактируйте:

    String text = "DOZER";

Измените на что-то другое, например: "WARNING" или "RUN"

Если вы хотите изменить позицию текста:

    int y = height - textHeight - 20; // 20 pixels from bottom

Измените 20 на другое значение для сдвига вверх/вниз.
*/

// ============================================================================
// ПРИМЕР 8: Проверка состояния игрока перед атакой
// ============================================================================

import com.GraceMC.logic.DozerManager;
import net.minecraft.server.network.ServerPlayerEntity;

public class DozerSafeAttack {
    public static void safeAttack(ServerPlayerEntity player) {
        // Проверяем, жив ли игрок
        if (!player.isAlive()) {
            return;
        }
        
        // Проверяем, в творческом режиме ли
        if (player.isCreative()) {
            return;
        }
        
        // Проверяем, в выживании ли
        if (player.interactionManager.isSurvivalLike()) {
            DozerManager.triggerDozerAttack(player);
        }
    }
}

// ============================================================================
// ПРИМЕЧАНИЯ
// ============================================================================

/*
ТРЕБУЕМЫЕ ФАЙЛЫ:
1. src/main/resources/assets/gracemc/textures/dozer_paper.png (текстура)
2. src/main/resources/assets/gracemc/sounds/dozer_attack.ogg (звук)

ПОВЕДЕНИЕ АТАКИ:
- Мгновенное появление и исчезновение
- Полноэкранная текстура
- Большой красный текст "DOZER" (5x масштаб)
- Страшный звук при появлении
- БЕЗ замедления времени или блокировки камеры
- Длительность: 40 тиков (~2 секунды)

СИНХРОНИЗАЦИЯ:
- Автоматическая синхронизация между сервером и клиентом
- Звук проигрывается на клиентской стороне
- Отрисовка происходит в HudRenderCallback

ПРОИЗВОДИТЕЛЬНОСТЬ:
- Минимальный оверхед
- Только 1 вызов текстуры за атаку
- Без анимаций или сложных вычислений
*/
