package dev.unethicalite.api.movement;

import dev.unethicalite.api.game.Game;
import dev.unethicalite.api.movement.pathfinder.GlobalCollisionMap;
import dev.unethicalite.api.movement.pathfinder.Transport;
import net.runelite.api.coords.WorldPoint;

import java.util.*;
import java.util.stream.Collectors;

import static dev.unethicalite.api.movement.pathfinder.TransportLoader.loadStaticTransports;

public class CoolerPathfinder {

    private static final Map<WorldPoint, List<Transport>> transports = getTransports();
    private static final GlobalCollisionMap cm = Game.getGlobalCollisionMap();

    public static List<WorldPoint> getPath(WorldPoint from, WorldPoint to) {
        Map<WorldPoint, WorldPoint> previous = new HashMap<>();
        Map<WorldPoint, Integer> gscore = new HashMap<>();
        Map<WorldPoint, Double> fscore = new HashMap<>();
        PriorityQueue<WorldPoint> queue = new PriorityQueue<>(Comparator.comparingDouble(fscore::get));

        gscore.put(from, 0);
        fscore.put(from, distanceTo(from, to));
        queue.add(from);

        while (!queue.isEmpty()) {
            WorldPoint current = queue.poll();
            if (current.equals(to)) {
                return buildPath(from, to, previous);
            }

            for (WorldPoint neighbor : getNeighbors(current)) {
                int alt = gscore.get(current) + 1;
                if (alt < gscore.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    gscore.put(neighbor, alt);
                    previous.put(neighbor, current);
                    fscore.put(neighbor, alt + distanceTo(neighbor, to));
                    queue.add(neighbor);
                }
            }
        }
        return List.of();
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

    public static double distanceTo(WorldPoint first, WorldPoint second) {
        return Math.hypot(Math.abs(first.getX() - second.getX()), Math.abs(first.getY() - second.getY()));
    }

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

        transports.getOrDefault(source, List.of()).forEach(transport -> out.add(transport.getDestination()));

        return out;
    }

    public static Map<WorldPoint, List<Transport>> getTransports() {
        return loadStaticTransports().stream().collect(Collectors.groupingBy(Transport::getSource));
    }

//    public static List<Teleport> getTeleports() {
//
//    }

}