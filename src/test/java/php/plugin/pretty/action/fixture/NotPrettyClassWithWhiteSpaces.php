<?php

use Shared\WidgetBundle\Entity\Widget;

use Shared\CallbackBundle\Dto\Service\Callback\Result\UpdateCallbackDto as UpdateCallbackResult;
use Symfony\Component\HttpKernel\Kernel;






use JMS\DiExtraBundle\Annotation as DI;
use Telephony\ApiBundle\Dto\Controller\Callback\Request\CreateCallbackDto;

class NotPrettyClass {
    public function bar()
    {
        echo 'bar';
    }
}