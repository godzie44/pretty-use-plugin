<?php

<weak_warning>use Symfony\Component\HttpKernel\Kernel;
use Shared\WidgetBundle\Entity\Widget;
use Shared\CallbackBundle\Dto\Service\Callback\Result\UpdateCallbackDto as UpdateCallbackResult;
use Telephony\ApiBundle\Dto\Controller\Callback\Request\CreateCallbackDto;
use JMS\DiExtraBundle\Annotation as DI;</weak_warning>

class NotPrettyClass {
    public function bar()
    {
        echo 'bar';
    }
}