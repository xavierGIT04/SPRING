package tg.nassara.estimation.dto.request;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Map;

/** Les valeurs peuvent être envoyées en JSON sous forme de chaînes, nombres ou booléens. */
public record CreationParametreRequest(
        @NotEmpty Map<String, String> valeurs,
        @Size(max = 500) String motif) {
}