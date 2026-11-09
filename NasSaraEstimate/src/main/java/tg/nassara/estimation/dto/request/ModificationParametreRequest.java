package tg.nassara.estimation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Map;

/** « version » (optionnelle) est la version lue par le client : si elle a changé, la modification est refusée. */
public record ModificationParametreRequest(
        Long version,
        @NotEmpty Map<String, String> valeurs,
        @Size(max = 500) String motif) {
}