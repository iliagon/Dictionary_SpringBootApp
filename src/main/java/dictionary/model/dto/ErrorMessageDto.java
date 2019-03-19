package dictionary.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Структура для отправки сообщения об ошибке.
 */
@Data
@Accessors(chain = true)
public class ErrorMessageDto {
  /**
   * Внутренний код ошибки
   */
  private String code = null;
  /**
   * Сообщение для разработчика
   */
  private String devMessage = null;
}

