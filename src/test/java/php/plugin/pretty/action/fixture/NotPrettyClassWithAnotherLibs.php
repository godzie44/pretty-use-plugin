<?php

use Symfony\Component\HttpKernel\Kernel;

use Shared\CallbackBundle\Dto\Service\Callback\Result\UpdateCallbackDto as UpdateCallbackResult;
use JMS\DiExtraBundle\Annotation as DI;
use Telephony\ApiBundle\Dto\Controller\Callback\Request\CreateCallbackDto;
use Shared\WidgetBundle\Entity\Widget;

class NotPrettyClass {
    public function bar()
    {
        echo 'bar';
    }
}