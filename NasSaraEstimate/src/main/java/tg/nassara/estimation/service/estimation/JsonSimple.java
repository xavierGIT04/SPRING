package tg.nassara.estimation.service.estimation;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Sérialisation JSON minimale (Map, liste, texte, nombre, booléen, énumération, null) pour le détail de calcul.
 * Écrite à la main pour ne pas dépendre de la version de Jackson (2 ou 3) fournie par Spring Boot.
 */
final class JsonSimple {

    private JsonSimple() {
    }

    static String versJson(Object valeur) {
        StringBuilder sb = new StringBuilder();
        ecrire(sb, valeur);
        return sb.toString();
    }

    private static void ecrire(StringBuilder sb, Object valeur) {
        if (valeur == null) {
            sb.append("null");
        } else if (valeur instanceof Boolean) {
            sb.append(valeur);
        } else if (valeur instanceof BigDecimal nombre) {
            sb.append(nombre.toPlainString());
        } else if (valeur instanceof Double || valeur instanceof Float) {
            double d = ((Number) valeur).doubleValue();
            sb.append(Double.isNaN(d) || Double.isInfinite(d) ? "null" : valeur.toString());
        } else if (valeur instanceof Number) {
            sb.append(valeur);
        } else if (valeur instanceof Map<?, ?> map) {
            sb.append('{');
            boolean premier = true;
            for (Map.Entry<?, ?> entree : map.entrySet()) {
                if (!premier) {
                    sb.append(',');
                }
                premier = false;
                texte(sb, String.valueOf(entree.getKey()));
                sb.append(':');
                ecrire(sb, entree.getValue());
            }
            sb.append('}');
        } else if (valeur instanceof Iterable<?> iterable) {
            sb.append('[');
            boolean premier = true;
            for (Object element : iterable) {
                if (!premier) {
                    sb.append(',');
                }
                premier = false;
                ecrire(sb, element);
            }
            sb.append(']');
        } else if (valeur instanceof Enum<?> e) {
            texte(sb, e.name());
        } else {
            texte(sb, valeur.toString());
        }
    }

    private static void texte(StringBuilder sb, String texte) {
        sb.append('"');
        for (int i = 0; i < texte.length(); i++) {
            char c = texte.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        sb.append('"');
    }
}
