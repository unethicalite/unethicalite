package dev.unethicalite.api.movement;

import dev.unethicalite.api.game.Game;
import dev.unethicalite.api.movement.pathfinder.GlobalCollisionMap;
import net.runelite.api.coords.WorldPoint;

import java.util.*;

public class CoolerPathfinder {

    private static final GlobalCollisionMap cm = Game.getGlobalCollisionMap();

    private static List<WorldPoint> getNeighbors(WorldPoint source) {
        List<WorldPoint> out = new ArrayList<>();
        try {
            if (cm.w(source.getX(), source.getY(), source.getPlane())) {
                out.add(source.dx(-1));
            }
        } catch (Exception ignored) {
        }
        try {
            if (cm.e(source.getX(), source.getY(), source.getPlane())) {
                out.add(source.dx(1));
            }
        } catch (Exception ignored) {
        }
        try {
            if (cm.s(source.getX(), source.getY(), source.getPlane())) {
                out.add(source.dy(-1));
            }
        } catch (Exception ignored) {
        }
        try {
            if (cm.n(source.getX(), source.getY(), source.getPlane())) {
                out.add(source.dy(1));
            }
        } catch (Exception ignored) {
        }
        try {
            if (cm.sw(source.getX(), source.getY(), source.getPlane())) {
                out.add(source.dx(-1).dy(-1));
            }
        } catch (Exception ignored) {
        }
        try {
            if (cm.se(source.getX(), source.getY(), source.getPlane())) {
                out.add(source.dx(1).dy(-1));
            }
        } catch (Exception ignored) {
        }
        try {
            if (cm.nw(source.getX(), source.getY(), source.getPlane())) {
                out.add(source.dx(-1).dy(1));
            }
        } catch (Exception ignored) {
        }
        try {
            if (cm.ne(source.getX(), source.getY(), source.getPlane())) {
                out.add(source.dx(1).dy(1));
            }
        } catch (Exception ignored) {
        }
        return out;
    }

    public static List<WorldPoint> getPath(WorldPoint from, WorldPoint to) {
        Set<WorldPoint> visited = new HashSet<>();
        Map<WorldPoint, Double> dist = new HashMap<>();
        Map<WorldPoint, WorldPoint> previous = new HashMap<>();
        PriorityQueue<WorldPoint> queue = new PriorityQueue<>(Comparator.comparingDouble(it -> distanceTo(it, to)));

        dist.put(from, 0.0);
        queue.add(from);

        while (!queue.isEmpty()) {
            WorldPoint current = queue.poll();
            if (current.equals(to)) {
                break;
            }
            for (WorldPoint neighbor : getNeighbors(current)) {
                if (visited.contains(neighbor)) {
                    continue;
                }
                visited.add(neighbor);
                double alt = dist.get(current) + distanceTo(neighbor, current);
                if (queue.contains(neighbor)) {
                    if (alt < dist.get(neighbor)) {
                        dist.put(neighbor, alt);
                        previous.put(neighbor, current);
                    }
                } else {
                    dist.put(neighbor, alt);
                    previous.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }
        return buildPath(from, to, previous);
    }

    public static double distanceTo(WorldPoint first, WorldPoint second) {
        return Math.hypot(Math.abs(first.getX() - second.getX()), Math.abs(first.getY() - second.getY()));
    }

    private static List<WorldPoint> buildPath(WorldPoint from, WorldPoint to, Map<WorldPoint, WorldPoint> previous) {
        List<WorldPoint> path = new ArrayList<>();
        WorldPoint current = to;
        while (!current.equals(from)) {
            path.add(current);
            current = previous.get(current);
        }
        Collections.reverse(path);
        return path;
    }

}