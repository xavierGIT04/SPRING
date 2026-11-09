package tg.nassara.estimation.dto.request;

import jakarta.validation.constraints.Size;

public record RestaurationRequest(@Size(max = 500) String motif) {
}
