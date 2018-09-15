<?php

<weak_warning>use Shared\WidgetBundle\Entity\Widget;
use Telephony\ApiBundle\Dto\Controller\Callback\Request\CreateCallbackDto;
use Shared\CallbackBundle\Dto\Service\Callback\Result\UpdateCallbackDto as UpdateCallbackResult;
use JMS\DiExtraBundle\Annotation as DI;
use Symfony\Component\HttpKernel\Kernel;</weak_warning>

class NotPrettyClass {
    public function bar()
    {
        echo 'bar';
    }
}